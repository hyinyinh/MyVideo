package com.hy.tiktok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/4/17 19:28
 */
@SpringBootApplication
@MapperScan(basePackages = "com.hy.tiktok.mapper")
@ComponentScan(basePackages = {"com.hy.tiktok","org.n3r.idworker"})
@EnableMongoRepositories
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
