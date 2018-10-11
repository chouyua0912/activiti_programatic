package z.learn;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ActivitiRun {

    private static ApplicationContext context;
    private static RuntimeService runtimeService;
    private static TaskService taskService;

    public static void main(String[] args) throws InterruptedException {
        context = new ClassPathXmlApplicationContext("activiti.xml");
        runtimeService = context.getBean("runtimeService", RuntimeService.class);
        taskService = context.getBean("taskService", TaskService.class);

        Thread.sleep(10 * 60 * 1000);
    }
}
