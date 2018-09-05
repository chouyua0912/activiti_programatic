package z.learn;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * spring integration of activiti
 * demo to programatically create an new flow
 *
 * 验证当前步骤，suspended状态时候execution的状态
 * select * from ACT_RU_EXECUTION;   execution有状态，当前Activity
 * active     SUSPENSION_STATE_ = 1
 * suspended  SUSPENSION_STATE_ = 2
 *
 * select * from ACT_RU_JOB;
 *
 * select * from ACT_HI_TASKINST;
 *
 * select * from ACT_RU_TASK;  task 是有状态的
 *
 * 步骤间流转通过 BpmnActivityBehavior 查找决定 take 那条路， FlowNodeActivityBehavior
 *
 * 验证点：暂停之后，异步设定的job是否会被结束，activity是否会被更新到新的步骤？？
 *
 * PROCESS_START
 * PROCESS_START_INITIAL
 * ACTIVITY_EXECUTE
 * TRANSITION_NOTIFY_LISTENER_END
 * TRANSITION_DESTROY_SCOPE
 * TRANSITION_NOTIFY_LISTENER_TAKE
 * TRANSITION_CREATE_SCOPE
 *                      ->TRANSITION_NOTIFY_LISTENER_START
 *                                  ->TRANSITION_CREATE_SCOPE
 *                                  ->ACTIVITY_EXECUTE
 */
public class Activiti {

    private static ApplicationContext context;
    private static RuntimeService runtimeService;
    private static TaskService taskService;

    public static void main(String[] args) throws InterruptedException {
        context = new ClassPathXmlApplicationContext("activiti.xml");
        runtimeService = context.getBean("runtimeService", RuntimeService.class);
        taskService = context.getBean("taskService", TaskService.class);

        ProcessInstance instance = runtimeService.startProcessInstanceByKey("flow3");       // 执行ｊｏｂ１的过程中会记录流程的ｒｅｖ，最后执行万去更新的时候如果版本不一致就会抛出并发更新异常


        System.out.println(Thread.currentThread().getName() + " started:" + instance.getId());
        Thread.sleep(2 * 1000);

        taskService.createTaskQuery().processInstanceId(instance.getProcessInstanceId()).list().forEach(t -> {
            taskService.complete(t.getId());        // complete 是会同步去执行后面的serviceTask 需要改成完全异步的
            System.out.println(Thread.currentThread().getName() + " Complete task id: " + t.getId());
        });

        Thread.sleep(10 * 1000);

        runtimeService.suspendProcessInstanceById(instance.getProcessInstanceId());                         // 暂停之后会更新ｅｘｅｃｕｔｉｏｎ的ｒｅｖ
        System.out.println(Thread.currentThread().getName() + " suspended:" + instance.getId());            // ｊｏｂ1执行完的时候去更新ｅｘｅｃｕｔｉｏｎ就会抛出并发更新异常，导致ｊｏｂ执行失败，所以会自动重试重新执行ｊｏｂ１
        Thread.sleep(30 * 1000);

        runtimeService.activateProcessInstanceById(instance.getProcessInstanceId());
        System.out.println(Thread.currentThread().getName() + " activated:" + instance.getId());
        Thread.sleep(10 * 60 * 1000);
    }
}
