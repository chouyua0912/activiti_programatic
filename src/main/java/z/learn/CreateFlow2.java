package z.learn;

import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.Execution;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

@Component
public class CreateFlow2 implements InitializingBean {

    private String flowKey = "flow2";

    public void createAndDeploy(String keyName) {
        StartEvent start = new StartEvent();
        start.setId(getUniqueKey());
        start.setName("start");

        EndEvent end = new EndEvent();
        end.setId(getUniqueKey());
        end.setName("end");

        UserTask task = new UserTask();
        task.setId(getUniqueKey());
        task.setName("task");

        ServiceTask job = new ServiceTask();
        job.setId(getUniqueKey());
        job.setName("job");
        job.setAsynchronous(true);
        job.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION);
        job.setImplementation("#{createFlow2.execute(execution)}");

        SequenceFlow startToTask = new SequenceFlow();
        startToTask.setSourceRef(start.getId());
        startToTask.setTargetRef(task.getId());
        startToTask.setName("flow1");

        SequenceFlow taskToJob = new SequenceFlow();
        taskToJob.setSourceRef(task.getId());
        taskToJob.setTargetRef(job.getId());
        taskToJob.setName("flow2");

        SequenceFlow jobToEnd = new SequenceFlow();
        jobToEnd.setSourceRef(job.getId());
        jobToEnd.setTargetRef(end.getId());
        jobToEnd.setName("flow3");

        Process process = new Process();
        process.setId(keyName);
        process.setName("first");
        process.addFlowElement(start);
        process.addFlowElement(startToTask);
        process.addFlowElement(task);
        process.addFlowElement(taskToJob);
        process.addFlowElement(job);
        process.addFlowElement(jobToEnd);
        process.addFlowElement(end);

        BpmnModel model = new BpmnModel();
        model.addProcess(process);

        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addBpmnModel(keyName + ".bpmn", model)
                .name(keyName)
                .deploy();
    }

    private String getUniqueKey() {
        return "sid-" + UUID.randomUUID().toString();
    }

    public void execute(Execution execution) {
        for (int i = 0; i < 30; i++) {
            System.out.println(Thread.currentThread().getName() + ": hello: " + i + " : " + execution.getActivityId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Resource
    private RepositoryService repositoryService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (repositoryService.createProcessDefinitionQuery().processDefinitionKey(flowKey).list().isEmpty()) {
            createAndDeploy(flowKey);
        }
    }
}
