package org.pet.home;

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @description:
 * @author: ${USER}
 * @date: ${DATE}
 **/
@SpringBootApplication//(exclude = DataSourceAutoConfiguration.class)
//@EnableScheduling  // 启用定时任务功能
@MapperScan("org.pet.home.mapper")
public class MyApplication {

    //Springboot的启动
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

}
