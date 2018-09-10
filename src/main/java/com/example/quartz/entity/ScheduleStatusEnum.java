package com.example.quartz.entity;

/**
 * Created by shirukai on 2018/9/4
 */
public enum ScheduleStatusEnum {
    ACTIVATED(1, "已激活"),
    INACTIVATED(0, "未激活");
    private int state;
    private String stateInfo;

    ScheduleStatusEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }
}
