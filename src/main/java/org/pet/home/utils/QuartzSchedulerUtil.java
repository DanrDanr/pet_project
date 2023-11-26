package org.pet.home.utils;

import org.pet.home.job.OrderExpirationJob;
import org.pet.home.service.impl.IOrderService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/22
 **/
public class QuartzSchedulerUtil {
    public static void startOrderExpirationJob(String orderNumber,IOrderService orderService) {
        try {
            // 获取调度器
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("orderService", orderService);
            jobDataMap.put("orderNumber", orderNumber);

            // 创建任务
            JobDetail jobDetail = JobBuilder.newJob(OrderExpirationJob.class)
                    .withIdentity("orderExpirationJob_" + orderNumber, "orderExpirationJobGroup")
                    .usingJobData(jobDataMap)
                    .build();

            // 创建触发器
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("orderExpirationTrigger_" + orderNumber, "orderExpirationTriggerGroup")
                    .startAt(DateBuilder.futureDate(5, DateBuilder.IntervalUnit.SECOND))
                    .build();

            // 将任务和触发器添加到调度器中
            scheduler.scheduleJob(jobDetail, trigger);

            // 启动调度器
            scheduler.start();
        } catch (SchedulerException e) {
            // 处理异常
        }
    }
}
