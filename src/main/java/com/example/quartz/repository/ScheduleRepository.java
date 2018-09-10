package com.example.quartz.repository;

import com.example.quartz.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by shirukai on 2018/9/4
 */
public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    Schedule findScheduleByJobNameAndGroupName(String jobName, String groupName);

    Schedule findScheduleById(String scheduleId);
}
