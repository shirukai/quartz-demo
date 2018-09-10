package com.example.quartz.job;

import com.example.quartz.entity.Schedule;
import com.example.quartz.repository.ScheduleRepository;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by shirukai on 2018/9/7
 */
public class HelloJob implements Job {
    @Autowired
    ScheduleRepository scheduleRepository;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //获取上下文数据
        JobDataMap dataMap = context.getMergedJobDataMap();
        String scheduleId = dataMap.getString("id");

        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);
        log.info("定时任务执行了：{}", scheduleId);
        //更新执行条数
        schedule.setRecord(schedule.getRecord() + 1);
        scheduleRepository.save(schedule);
    }
}
