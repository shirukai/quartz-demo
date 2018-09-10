package com.example.quartz.controller;


import com.example.quartz.common.rest.RestMessage;
import com.example.quartz.common.util.RestMessageUtil;
import com.example.quartz.dto.CronScheduleDTO;
import com.example.quartz.dto.SimpleScheduleDTO;
import com.example.quartz.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by shirukai on 2018/9/7
 */
@RestController
@RequestMapping(value = "/api/v1/schedule/")
public class ScheduleController {
    @Autowired
    ScheduleService scheduleService;

    @PostMapping(value = "/{jobName}/cron")
    public RestMessage schedule(
            @PathVariable("jobName") String jobName,
            @RequestBody CronScheduleDTO cronScheduleDTO
    ) {
        return RestMessageUtil.objectToRestMessage(scheduleService.setSchedule(jobName, cronScheduleDTO));
    }

    @PutMapping(value = "/{jobName}/cron")
    public RestMessage modifySchedule(
            @PathVariable("jobName") String jobName,
            @RequestBody CronScheduleDTO cronScheduleDTO
    ) {
        return RestMessageUtil.objectToRestMessage(scheduleService.modifySchedule(jobName, cronScheduleDTO));
    }

    @PostMapping(value = "/{jobName}/simple")
    public RestMessage schedule(
            @PathVariable("jobName") String jobName,
            @RequestBody SimpleScheduleDTO simpleScheduleDTO
    ) {
        return RestMessageUtil.objectToRestMessage(scheduleService.setSchedule(jobName, simpleScheduleDTO));
    }

    @PutMapping(value = "/{jobName}/simple")
    public RestMessage modifySchedule(
            @PathVariable("jobName") String jobName,
            @RequestBody SimpleScheduleDTO simpleScheduleDTO
    ) {
        return RestMessageUtil.objectToRestMessage(scheduleService.modifySchedule(jobName, simpleScheduleDTO));
    }

    @DeleteMapping(value = "/{jobName}")
    public RestMessage removeSchedule(
            @PathVariable("jobName") String jobName
    ) {
        return RestMessageUtil.objectToRestMessage(scheduleService.removerSchedule(jobName));
    }

    @PostMapping(value = "/{jobName}/pause")
    public RestMessage pauseSchedule(
            @PathVariable("jobName") String jobName
    ) {
        return RestMessageUtil.objectToRestMessage(scheduleService.pauseSchedule(jobName));
    }

    @PostMapping(value = "/{jobName}/resume")
    public RestMessage resumeSchedule(
            @PathVariable("jobName") String jobName
    ) {
        return RestMessageUtil.objectToRestMessage(scheduleService.resumeSchedule(jobName));
    }

    @GetMapping(value = "/{jobName}")
    public RestMessage scheduleInfo(
            @PathVariable("jobName") String jobName
    ) {
        return RestMessageUtil.objectToRestMessage(scheduleService.getSchedule(jobName));
    }
}
