package com.tmall.mq;

import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;



/**
 * @Program: tmall_springboot
 * @Description:
 * @Author: YaYa
 * @Create: 2020-07-07 16:33
 */
@Configuration
public class MqConfig {

    public static final String MIAO_SHA_QUEUE="miaosha.queue";
    public static final String QUEUE="queue";
    public static final String MIAOSHA_EXCHANGE = "miaosha.exchange";

    /**
     * Direct模式 交换机Exchange
     * */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

}
