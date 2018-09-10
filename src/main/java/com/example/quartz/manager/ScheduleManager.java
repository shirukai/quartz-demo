package com.example.quartz.manager;

import com.alibaba.fastjson.JSON;
import com.example.quartz.dto.CronScheduleDTO;
import com.example.quartz.dto.Period;
import com.example.quartz.dto.ScheduleDTO;
import com.example.quartz.dto.SimpleScheduleDTO;
import com.example.quartz.entity.Schedule;
import com.example.quartz.entity.ScheduleStatusEnum;
import com.example.quartz.repository.ScheduleRepository;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
/**
 * Created by shirukai on 2018/9/7
 * 定时任务管理器
 */
@Component
public class ScheduleManager {
    @Autowired
    Scheduler scheduler;
    @Autowired
    ScheduleRepository scheduleRepository;
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    /**
     * 创建 simple schedule job
     *
     * @param jobClass   job class
     * @param ssd        参数
     * @param jobDataMap 数据
     * @return Schedule
     */
    public Schedule createSimpleJob(Class<? extends Job> jobClass,
                                    SimpleScheduleDTO ssd,
                                    JobDataMap jobDataMap) {
        Trigger trigger = getSimpleTrigger(ssd);
        return createJob(jobClass, ssd, jobDataMap, trigger);
    }

    /**
     * 创建 cron schedule job
     *
     * @param jobClass   可执行job class
     * @param csd        定时参数
     * @param jobDataMap 数据
     * @return Schedule
     */
    public Schedule createCronJob(Class<? extends Job> jobClass, CronScheduleDTO csd, JobDataMap jobDataMap) {
        Trigger trigger = getCronTrigger(csd);
        return createJob(jobClass, csd, jobDataMap, trigger);
    }

    /**
     * 创建Job
     *
     * @param jobClass   要调度的类名
     * @param sd         调度参数
     * @param jobDataMap 数据
     * @param trigger    trigger
     * @return Schedule
     */
    private Schedule createJob(
            Class<? extends Job> jobClass,
            ScheduleDTO sd,
            JobDataMap jobDataMap,
            Trigger trigger
    ) {
        String jobName = sd.getJobName();
        String group = sd.getGroup();
        //判断记录在数据库是否存在
        Schedule schedule = scheduleRepository.findScheduleByJobNameAndGroupName(jobName, group);
        if (schedule == null) {
            schedule = new Schedule();
        } else {
            throw new RuntimeException("Schedule job already exists.");
        }
        String scheduleId = UUID.randomUUID().toString();
        try {
            if (jobDataMap == null) {
                jobDataMap = new JobDataMap();
            }
            jobDataMap.put("id", scheduleId);
            //创建JobDetail
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, group).usingJobData(jobDataMap).build();
            schedule.setId(scheduleId);
            schedule.setStatus(ScheduleStatusEnum.ACTIVATED);
            schedule.setJobName(jobName);
            schedule.setGroupName(group);
            schedule.setTriggerInfo(JSON.toJSONString(sd));
            schedule.setRecord(0);
            //保存记录信息
            schedule = scheduleRepository.save(schedule);
            //调度执行定时任务
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error("Create schedule job error:{}", e.getMessage());
            throw new RuntimeException(e);
        }
        return schedule;
    }

    /**
     * 更新simple job
     *
     * @param scheduleId scheduleId
     * @param ssd        ssv
     * @return Schedule
     */
    public Schedule updateSimpleJob(String scheduleId, SimpleScheduleDTO ssd) {
        Schedule schedule = getSchedule(scheduleId);
        return updateSimpleJob(schedule, ssd);
    }

    public Schedule updateSimpleJob(Schedule schedule, SimpleScheduleDTO ssd) {
        try {
            String jobName = schedule.getJobName();
            String groupName = schedule.getGroupName();
            JobKey jobKey = new JobKey(jobName, groupName);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            //先删除
            scheduler.deleteJob(jobKey);
            //重新创建
            Trigger trigger = getSimpleTrigger(ssd);
            scheduler.scheduleJob(jobDetail, trigger);
            //更新元数据
            schedule.setRecord(0);
            schedule.setTriggerInfo(JSON.toJSONString(ssd));
            scheduleRepository.save(schedule);
        } catch (SchedulerException e) {
            log.error("Update simple schedule job error:{}", e.getMessage());
        }
        return schedule;
    }

    public Schedule updateCronJob(String scheduleId, CronScheduleDTO csd) {
        Schedule schedule = getSchedule(scheduleId);
        return updateCronJob(schedule, csd);
    }

    public Schedule updateCronJob(Schedule schedule, CronScheduleDTO csd) {
        try {
            String jobName = schedule.getJobName();
            String groupName = schedule.getGroupName();
            JobKey jobKey = new JobKey(jobName, groupName);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            //先删除
            scheduler.deleteJob(jobKey);
            //重新创建
            Trigger trigger = getCronTrigger(csd);
            scheduler.scheduleJob(jobDetail, trigger);
            //更新元数据
            schedule.setRecord(0);
            schedule.setTriggerInfo(JSON.toJSONString(csd));
            scheduleRepository.save(schedule);
        } catch (SchedulerException e) {
            log.error("Update cron schedule job error:{}", e.getMessage());
        }
        return schedule;
    }


    public Schedule getSchedule(String scheduleId) {
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("Schedule job does not exist");
        }
        return schedule;
    }

    /**
     * 暂停某个job
     *
     * @param scheduleId id
     */
    public Schedule pauseJob(String scheduleId) {
        Schedule schedule = getSchedule(scheduleId);
        return pauseJob(schedule);
    }

    public Schedule pauseJob(Schedule schedule) {
        JobKey jobKey = new JobKey(schedule.getJobName(), schedule.getGroupName());
        try {
            scheduler.pauseJob(jobKey);
            schedule.setStatus(ScheduleStatusEnum.INACTIVATED);
            scheduleRepository.save(schedule);
        } catch (SchedulerException e) {
            log.error("Pause schedule job error:{}", e.getMessage());
        }
        return schedule;
    }

    /**
     * 恢复某个job
     *
     * @param scheduleId id
     */
    public Schedule resumeJob(String scheduleId) {
        Schedule schedule = getSchedule(scheduleId);
        return resumeJob(schedule);
    }

    public Schedule resumeJob(Schedule schedule) {
        JobKey jobKey = new JobKey(schedule.getJobName(), schedule.getGroupName());
        try {
            scheduler.resumeJob(jobKey);
            schedule.setStatus(ScheduleStatusEnum.ACTIVATED);
            scheduleRepository.save(schedule);
        } catch (SchedulerException e) {
            log.error("Resume schedule job error:{}", e.getMessage());
        }
        return schedule;
    }

    /**
     * 删除 job
     *
     * @param scheduleId id
     */
    public void deleteJob(String scheduleId) {
        Schedule schedule = getSchedule(scheduleId);
        deleteJob(schedule);
    }

    public void deleteJob(Schedule schedule) {
        JobKey jobKey = new JobKey(schedule.getJobName(), schedule.getGroupName());
        try {
            scheduler.deleteJob(jobKey);
            scheduleRepository.delete(schedule);
        } catch (SchedulerException e) {
            log.error("Delete schedule job error:{}", e.getMessage());
        }
    }

    public Schedule getJobByNameAndGroup(String name, String group) {
        Schedule schedule = scheduleRepository.findScheduleByJobNameAndGroupName(name, group);
        if (schedule == null) {
            throw new RuntimeException("Schedule job does not exist");
        }
        return schedule;
    }

    /**
     * 构建 SimpleScheduleBuilder
     *
     * @param period 周期参数
     * @return SimpleScheduleBuilder
     */
    private SimpleScheduleBuilder getSimpeScheduleBuilder(Period period, int repeatCount) {
        SimpleScheduleBuilder ssb = SimpleScheduleBuilder.simpleSchedule();
        String unit = period.getUnit();
        long time = period.getTime();
        switch (unit) {
            case "milliseconds":
                ssb.withIntervalInMilliseconds(time);
                break;
            case "seconds":
                ssb.withIntervalInSeconds((int) time);
                break;
            case "minutes":
                ssb.withIntervalInMinutes((int) time);
                break;
            case "hours":
                ssb.withIntervalInHours((int) time);
                break;
            case "days":
                ssb.withIntervalInHours((int) time * 24);
                break;
            default:
                break;
        }
        ssb.withRepeatCount(repeatCount);
        return ssb;
    }

    private Trigger getCronTrigger(CronScheduleDTO csd) {
        CronScheduleBuilder scb = CronScheduleBuilder.cronSchedule(csd.getCronExpression());
        TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(csd.getJobName(), csd.getGroup())
                .withSchedule(scb);
        if (csd.getStartTime() != 0) {
            triggerBuilder.startAt(new Date(csd.getStartTime()));
        } else {
            triggerBuilder.startNow();
        }
        if (csd.getEndTime() != 0) {
            triggerBuilder.endAt(new Date(csd.getEndTime()));
        }
        return triggerBuilder.build();
    }


    /**
     * 构建 SimpleTrigger
     *
     * @param ssd 参数
     * @return Trigger
     */
    private Trigger getSimpleTrigger(SimpleScheduleDTO ssd) {
        String jobName = ssd.getJobName();
        String group = ssd.getGroup();
        int repeatCount = ssd.getRepeatCount();
        TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger()
                //设置jobName和group
                .withIdentity(jobName, group)
                //设置Schedule方式
                .withSchedule(getSimpeScheduleBuilder(ssd.getPeriod(), repeatCount));
        if (ssd.getStartTime() != 0) {
            //设置起始时间
            triggerBuilder.startAt(new Date(ssd.getStartTime()));
        } else {
            triggerBuilder.startNow();
        }
        if (ssd.getEndTime() != 0) {
            //设置终止时间
            triggerBuilder.endAt(new Date(ssd.getEndTime()));
        }
        return triggerBuilder.build();
    }

}
