package org.pet.home.job;
import org.pet.home.service.impl.IOrderService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @description:
 * @author: 22866
 * @date: 2023/11/22
 **/
public class OrderExpirationJob implements Job {
    private Logger logger = LoggerFactory.getLogger(OrderExpirationJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String orderNumber = jobExecutionContext.getJobDetail().getJobDataMap().getString("orderNumber");
        logger.info("number=======" + orderNumber);
        IOrderService orderService = (IOrderService) jobExecutionContext.getJobDetail().getJobDataMap().get("orderService");
        if (orderService == null) {
            logger.error("orderService is null");
        } else {
            logger.info("orderService is not null");
        }
        // 根据订单号处理订单过期逻辑
        if (orderService != null) {
            orderService.cancelOrder(2, orderNumber);
        }
    }
}
