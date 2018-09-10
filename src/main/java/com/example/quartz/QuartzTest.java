package com.example.quartz;

import com.example.quartz.job.HelloJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by shirukai on 2018/9/6
 */
public class QuartzTest {
    public static void main(String[] args) throws Exception {
        //从工厂创建scheduler实例
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        //开启scheduler
        scheduler.start();
        //定义一个job，并绑定我们的HelloJob
        JobDetail jobDetail = newJob(HelloJob.class)
                .withIdentity("job1", "group1")
                .build();
        //定义一个simple trigger,设置重复次数为10次，周期为2秒
        Trigger trigger = newTrigger()
                .withIdentity("job1", "group1")
                .startNow()
                .withSchedule(simpleSchedule().withIntervalInSeconds(1).withRepeatCount(10)).build();
        //使用scheduler进行job调度
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
