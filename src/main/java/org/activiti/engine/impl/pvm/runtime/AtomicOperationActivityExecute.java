/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.engine.impl.pvm.runtime;

import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.pvm.PvmException;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.logging.LogMDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Tom Baeyens
 */
public class AtomicOperationActivityExecute implements AtomicOperation {

    private static Logger log = LoggerFactory.getLogger(AtomicOperationActivityExecute.class);

    public boolean isAsync(InterpretableExecution execution) {
        return false;
    }

    public void execute(InterpretableExecution execution) {
        ActivityImpl activity = (ActivityImpl) execution.getActivity();

        ActivityBehavior activityBehavior = activity.getActivityBehavior(); // 外挂的实际操作 比如：ServiceTaskExpressionActivityBehavior 来启动Job
        if (activityBehavior == null) {
            throw new PvmException("no behavior specified in " + activity);
        }

        log.debug("{} executes {}: {}", execution, activity, activityBehavior.getClass().getName());

        try {
            if (Context.getProcessEngineConfiguration() != null                 // 发布事件通知
                    && Context.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
                Context.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
                        ActivitiEventBuilder.createActivityEvent(ActivitiEventType.ACTIVITY_STARTED,
                                execution.getActivity().getId(),
                                (String) execution.getActivity().getProperty("name"),
                                execution.getId(),
                                execution.getProcessInstanceId(),
                                execution.getProcessDefinitionId(),
                                (String) activity.getProperties().get("type"),
                                activity.getActivityBehavior().getClass().getCanonicalName()));
            }
            // Activiti节点（开始、结束、任务、网关等等）都是Activity类型的，只是其挂的ActivityBehavior不同，通过不同的ActivityBehavior来实现相应的操作。
            activityBehavior.execute(execution);    // InterpretableExecution继承了ActivityExecution，实现类是ExecutionEntity， 通过behavior.execute操作来串联
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            LogMDC.putMDCExecution(execution);
            throw new PvmException("couldn't execute activity <" + activity.getProperty("type") + " id=\"" + activity.getId() + "\" ...>: " + e.getMessage(), e);
        }
    }
}
