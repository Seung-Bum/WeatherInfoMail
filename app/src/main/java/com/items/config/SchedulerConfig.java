package com.items.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration //  설정파일을 만들기 위한 애노테이션 or Bean을 등록하기 위한 애노테이션이다.
public class SchedulerConfig implements SchedulingConfigurer{
	
    @Value("${thread.pool.size}")
    private int POOL_SIZE;
 
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
 
        scheduler.setPoolSize(POOL_SIZE);
        scheduler.setThreadNamePrefix("현재 쓰레드-");
        scheduler.initialize();
 
        taskRegistrar.setTaskScheduler(scheduler);
    }
}
