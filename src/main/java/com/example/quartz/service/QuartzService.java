package com.example.quartz.service;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

/**
 * Created by shirukai on 2018/9/6
 */
@Service
public class QuartzService {
    public void printJobInfo(JobExecutionContext context) {
        //从上下文中获取JobDetail
        JobDetail jobDetail = context.getJobDetail();
        String jobName = jobDetail.getKey().getName();
        String group = jobDetail.getKey().getGroup();
        System.out.println("Schedule job name is:" + jobName);
        System.out.println("Schedule job group is:" + group);
    }
}
