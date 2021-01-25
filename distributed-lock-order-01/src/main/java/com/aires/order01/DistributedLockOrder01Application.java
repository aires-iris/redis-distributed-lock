package com.aires.order01;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author sunsh
 */
@Slf4j
@SpringBootApplication
public class DistributedLockOrder01Application {




    public static void main(String[] args) {
        SpringApplication.run(DistributedLockOrder01Application.class, args);
    }

}
