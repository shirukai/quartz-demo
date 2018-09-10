package com.example.quartz.dto;

/**
 * Created by shirukai on 2018/9/7
 */
public class SimpleScheduleDTO extends ScheduleDTO {
    private int repeatCount;
    private Period period;

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }


    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }
}
