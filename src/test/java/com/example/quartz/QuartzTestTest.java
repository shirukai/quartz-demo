package com.example.quartz;

import com.example.quartz.job.HelloJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by shirukai on 2018/9/6
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class QuartzTestTest {
    @Autowired
    Scheduler scheduler;
    @Test
    public void main() throws Exception {
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