package z.learn;

import org.activiti.engine.RuntimeService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * spring integration of activiti
 * demo to programatically create an new flow
 */
public class Activiti {

    private static ApplicationContext context;
    private static RuntimeService runtimeService;
    private static String key = "demo";

    public static void main(String[] args) throws InterruptedException {
        context = new ClassPathXmlApplicationContext("activiti.xml");
        runtimeService = context.getBean("runtimeService", RuntimeService.class);
        runtimeService.startProcessInstanceByKey(key);

        Thread.sleep(10 * 60 * 1000);
    }

    private static void prepare() {
        CreateAndDeployFlow createAndDeployFlow = context.getBean("createAndDeployFlow", CreateAndDeployFlow.class);
        createAndDeployFlow.createAndDeploy(key);
    }
}
