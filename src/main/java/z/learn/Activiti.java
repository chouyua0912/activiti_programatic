package z.learn;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * spring integration of activiti
 * demo to programatically create an new flow
 */
public class Activiti {

    private static ApplicationContext context;
    private static RuntimeService runtimeService;
    private static TaskService taskService;

    public static void main(String[] args) throws InterruptedException {
        context = new ClassPathXmlApplicationContext("activiti.xml");
        runtimeService = context.getBean("runtimeService", RuntimeService.class);
        taskService = context.getBean("taskService", TaskService.class);

        ProcessInstance instance = runtimeService.startProcessInstanceByKey("flow2");


        System.out.println(Thread.currentThread().getName() + " started:" + instance.getId());
        Thread.sleep(2 * 1000);

        taskService.createTaskQuery().processInstanceId(instance.getProcessInstanceId()).list().forEach(t -> {
            taskService.complete(t.getId());        // complete 是会同步去执行后面的serviceTask 需要改成完全异步的
            System.out.println(Thread.currentThread().getName() + " Complete task id: " + t.getId());
        });

        Thread.sleep(2 * 1000);

        runtimeService.suspendProcessInstanceById(instance.getProcessInstanceId());
        System.out.println(Thread.currentThread().getName() + " suspended:" + instance.getId());
        Thread.sleep(2 * 1000);

        runtimeService.activateProcessInstanceById(instance.getProcessInstanceId());
        System.out.println(Thread.currentThread().getName() + " activated:" + instance.getId());
        Thread.sleep(10 * 60 * 1000);
    }
}
