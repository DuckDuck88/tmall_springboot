package com.tmall.mq;

import com.tmall.util.String2Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Program: tmall_springboot
 * @Description: s
 * @Author: YaYa
 * @Create: 2020-07-07 16:43
 */
@Component
public class MqSender {
    private static final Logger log = LoggerFactory.getLogger(MqSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendMessage(MqMessage mqMessage){
        //将bean转换为字符串
        String msg= String2Bean.beanToString(mqMessage);
        log.info("send message:"+msg);
        amqpTemplate.convertAndSend(MqConfig.MIAO_SHA_QUEUE, msg);
    }
}
