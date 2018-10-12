package z.learn;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

public class ActivitiShutdown {

    private static ApplicationContext context;
    private static RuntimeService runtimeService;
    private static TaskService taskService;

    public static void main(String[] args) throws InterruptedException {
        context = new ClassPathXmlApplicationContext("activiti.xml");
        runtimeService = context.getBean("runtimeService", RuntimeService.class);
        taskService = context.getBean("taskService", TaskService.class);
        ProcessEngine processEngine = context.getBean(ProcessEngine.class);

        ProcessInstance instance = runtimeService.startProcessInstanceByKey("flow555");       // 执行ｊｏｂ１的过程中会记录流程的ｒｅｖ，最后执行万去更新的时候如果版本不一致就会抛出并发更新异常

        System.out.println(Thread.currentThread().getName() + " started: FlowInstanceId = " + instance.getId() + " " + new Date());
        Thread.sleep(30 * 1000);

        System.out.println(Thread.currentThread().getName() + " close start at:" + new Date());
        processEngine.close();      // 会阻塞线程，同步等Activitiy关闭完成

        System.out.println(Thread.currentThread().getName() + " close finished, start wait at:" + new Date());
        Thread.sleep(10 * 60 * 1000);
    }
}
