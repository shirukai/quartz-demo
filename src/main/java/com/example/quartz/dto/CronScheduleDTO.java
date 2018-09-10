package com.example.quartz.dto;

/**
 * Created by shirukai on 2018/9/7
 */
public class CronScheduleDTO extends ScheduleDTO {
    private String cronExpression;

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
