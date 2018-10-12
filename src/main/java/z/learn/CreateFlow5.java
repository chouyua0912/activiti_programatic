package z.learn;

import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.Execution;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;

@Component
public class CreateFlow5 implements InitializingBean {
    private String flowKey = "flow555";

    public void createAndDeploy(String keyName) {
        StartEvent start = new StartEvent();
        start.setId("Sid-start");
        start.setName("start");

        EndEvent end = new EndEvent();
        end.setId("Sid-end");
        end.setName("end");

        ServiceTask job1 = new ServiceTask();
        job1.setId("Sid-0001-job01");
        job1.setName("job0001");
        job1.setAsynchronous(true);
        job1.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION);
        job1.setImplementation("#{createFlow5.execute1(execution, 'job111111')}");

        UserTask task2 = new UserTask();
        task2.setId("Sid-0002-task01");
        task2.setName("task0002");
        task2.setAsynchronous(true);
        ActivitiListener listener = new ActivitiListener();
        listener.setEvent("create");
        listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION);
        listener.setImplementation("#{createFlow5.execute2(execution,'Job22222_Task')}");
        task2.setTaskListeners(Collections.singletonList(listener));

        ServiceTask job3 = new ServiceTask();
        job3.setId("Sid-0003-job03");
        job3.setName("job0003");
        job3.setAsynchronous(true);
        job3.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION);
        job3.setImplementation("#{createFlow5.execute1(execution,'Job33333')}");

        SequenceFlow startToJob1 = new SequenceFlow();
        startToJob1.setSourceRef(start.getId());
        startToJob1.setTargetRef(job1.getId());
        startToJob1.setName("seq1");

        SequenceFlow jobToTask = new SequenceFlow();
        jobToTask.setSourceRef(job1.getId());
        jobToTask.setTargetRef(task2.getId());
        jobToTask.setName("seq2");

        SequenceFlow taskToJob3 = new SequenceFlow();
        taskToJob3.setSourceRef(task2.getId());
        taskToJob3.setTargetRef(job3.getId());
        taskToJob3.setName("seq3");

        SequenceFlow job3ToEnd = new SequenceFlow();
        job3ToEnd.setSourceRef(job3.getId());
        job3ToEnd.setTargetRef(end.getId());
        job3ToEnd.setName("seq4");

        org.activiti.bpmn.model.Process process = new Process();
        process.setId(keyName);
        process.setName("first");
        process.addFlowElement(start);
        process.addFlowElement(startToJob1);
        process.addFlowElement(job1);

        process.addFlowElement(jobToTask);
        process.addFlowElement(task2);


        process.addFlowElement(taskToJob3);
        process.addFlowElement(job3);

        process.addFlowElement(job3ToEnd);
        process.addFlowElement(end);

        BpmnModel model = new BpmnModel();
        model.addProcess(process);

        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addBpmnModel(keyName + ".bpmn", model)
                .name(keyName)
                .deploy();
    }

    public void execute1(Execution execution, String msg) {
        for (int i = 0; i < 60; i++) {
            System.out.println("Execute11 | " + execution.getProcessInstanceId() + " |" + msg + " - " + Thread.currentThread().getName() + ": hello: " + i + " : " + execution.getActivityId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void execute2(Execution execution, String msg) {
        for (int i = 0; i < 60; i++) {
            System.out.println("Execute22 | " + execution.getProcessInstanceId() + " |" + msg + " - " + Thread.currentThread().getName() + ": hello: " + i + " : " + execution.getActivityId());
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
