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
public class CreateFlow3 implements InitializingBean {

    private String flowKey = "flow3";

    public void createAndDeploy(String keyName) {
        StartEvent start = new StartEvent();
        start.setId(getUniqueKey());
        start.setName("start");

        EndEvent end = new EndEvent();
        end.setId(getUniqueKey());
        end.setName("end");

        ServiceTask job = new ServiceTask();
        job.setId(getUniqueKey());
        job.setName("job");
        job.setAsynchronous(true);
        job.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION);
        job.setImplementation("#{createFlow3.execute(execution, 'job1')}");

        ServiceTask job2 = new ServiceTask();
        job2.setId(getUniqueKey());
        job2.setName("job2");
        job2.setAsynchronous(true);
        job2.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_EXPRESSION);
        job2.setImplementation("#{createFlow3.execute(execution, 'job2')}");

        SequenceFlow startToJob = new SequenceFlow();
        startToJob.setSourceRef(start.getId());
        startToJob.setTargetRef(job.getId());
        startToJob.setName("flow1");

        SequenceFlow jobToJob = new SequenceFlow();
        jobToJob.setSourceRef(job.getId());
        jobToJob.setTargetRef(job2.getId());
        jobToJob.setName("flow2");

        SequenceFlow jobToEnd = new SequenceFlow();
        jobToEnd.setSourceRef(job2.getId());
        jobToEnd.setTargetRef(end.getId());
        jobToEnd.setName("flow3");

        Process process = new Process();
        process.setId(keyName);
        process.setName("first");
        process.addFlowElement(start);
        process.addFlowElement(startToJob);
        process.addFlowElement(job);
        process.addFlowElement(jobToJob);
        process.addFlowElement(job2);
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

    public void execute(Execution execution, String msg) {
        for (int i = 0; i < 30; i++) {
            System.out.println(Thread.currentThread().getName() + ": Check: " + msg + " " + i + " : " + execution.getActivityId());
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
