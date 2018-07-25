package z.learn;

import org.activiti.engine.RuntimeService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * spring integration of activiti
 * demo to programatically create an new flow
 */
public class Activiti {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("activiti.xml");
        RuntimeService runtimeService = context.getBean("runtimeService", RuntimeService.class);
        CreateAndDeployFlow createAndDeployFlow = context.getBean("createAndDeployFlow", CreateAndDeployFlow.class);

        String key = "demo";
        createAndDeployFlow.createAndDeploy(key);
        runtimeService.startProcessInstanceByKey(key);
        System.out.println("aaa");
    }
}
