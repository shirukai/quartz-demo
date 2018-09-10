package com.example.quartz.service;

import com.example.quartz.dto.CronScheduleDTO;
import com.example.quartz.dto.SimpleScheduleDTO;
import com.example.quartz.entity.Schedule;
import com.example.quartz.job.HelloJob;
import com.example.quartz.manager.ScheduleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by shirukai on 2018/9/7
 */
@Service
public class ScheduleService {
    private final static String GROUP = "TEST_GROUP1";
    @Autowired
    ScheduleManager scheduleManager;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 设置编排定时任务
     *
     * @param ssd 定时参数
     * @return schedule
     */
    public Schedule setSchedule(String joName, SimpleScheduleDTO ssd) {
        ssd.setJobName(joName);
        ssd.setGroup(GROUP);
        return scheduleManager.createSimpleJob(HelloJob.class, ssd, null);
    }

    public Schedule setSchedule(String jobName, CronScheduleDTO csd) {
        csd.setJobName(jobName);
        csd.setGroup(GROUP);
        return scheduleManager.createCronJob(HelloJob.class, csd, null);
    }

    /**
     * 更新编排定时任务
     *
     * @param ssd 参数
     * @return schedule
     */
    public Schedule modifySchedule(String jobName, SimpleScheduleDTO ssd) {
        Schedule schedule = scheduleManager.getJobByNameAndGroup(jobName, GROUP);
        ssd.setJobName(jobName);
        ssd.setGroup(GROUP);
        return scheduleManager.updateSimpleJob(schedule, ssd);
    }

    public Schedule modifySchedule(String jobName, CronScheduleDTO csd) {
        Schedule schedule = scheduleManager.getJobByNameAndGroup(jobName, GROUP);
        csd.setJobName(jobName);
        csd.setGroup(GROUP);
        return scheduleManager.updateCronJob(schedule, csd);
    }


    /**
     * 获取编排定时信息
     *
     * @param joName id
     * @return schedule
     */
    public Schedule getSchedule(String joName) {
        return scheduleManager.getJobByNameAndGroup(joName, GROUP);
    }

    /**
     * 暂停编排定时任务
     *
     * @param joName joName
     * @return schedule
     */
    public Schedule pauseSchedule(String joName) {
        Schedule schedule = scheduleManager.getJobByNameAndGroup(joName, GROUP);
        return scheduleManager.pauseJob(schedule);
    }

    /**
     * 恢复编排定时任务
     *
     * @param joName joName
     * @return schedule
     */
    public Schedule resumeSchedule(String joName) {
        Schedule schedule = scheduleManager.getJobByNameAndGroup(joName, GROUP);
        return scheduleManager.resumeJob(schedule);
    }

    /**
     * 删除编排定时任务
     *
     * @param joName joName
     * @return str
     */
    public String removerSchedule(String joName) {
        Schedule schedule = scheduleManager.getJobByNameAndGroup(joName, GROUP);
        scheduleManager.deleteJob(schedule);
        return "Delete schedule job succeed.";
    }
}
