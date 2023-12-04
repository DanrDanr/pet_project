package org.pet.home.config;

import org.pet.home.job.OrderExpirationJob;
import org.pet.home.service.impl.IOrderService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

/**
 * @description: 20秒后触发过期任务
 * @author: 22866
 * @date: 2023/11/22
 **/
@Configuration
public class QuartzConfig {
    // Quartz配置文件路径
    private static final String QUARTZ_CONFIG = "application.yaml";

    @Value("${task.enabled:true}")
    private boolean enabled;

    private DataSource dataSource;
    private IOrderService orderService;

    @Autowired
    public QuartzConfig(DataSource dataSource, IOrderService orderService) {
        this.dataSource = dataSource;
        this.orderService = orderService;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(dataSource);
        // 设置加载的配置文件
        schedulerFactoryBean.setConfigLocation(new ClassPathResource(QUARTZ_CONFIG));

        // 用于quartz集群,QuartzScheduler 启动时更新己存在的Job
        schedulerFactoryBean.setOverwriteExistingJobs(true);

        schedulerFactoryBean.setStartupDelay(5);// 系统启动后，延迟5s后启动定时任务，默认为0

        // 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        schedulerFactoryBean.setOverwriteExistingJobs(true);

        // SchedulerFactoryBean在初始化后是否马上启动Scheduler，默认为true。如果设置为false，需要手工启动Scheduler
        schedulerFactoryBean.setAutoStartup(enabled);
        return schedulerFactoryBean;
    }
    @Bean
    public JobDetail orderExpirationJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("orderService", orderService);

        JobDetail jobDetail = JobBuilder.newJob(OrderExpirationJob.class)
                .withIdentity("orderExpirationJob")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();

        return jobDetail;
    }

    @Bean
    public Trigger orderExpirationTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(orderExpirationJobDetail())
                .withIdentity("orderExpirationTrigger")
                .startAt(DateBuilder.futureDate(5, DateBuilder.IntervalUnit.SECOND)) // 30分钟后触发
                .build();
    }
}
