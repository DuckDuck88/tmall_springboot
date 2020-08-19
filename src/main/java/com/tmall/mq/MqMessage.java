package com.tmall.mq;

import com.tmall.pojo.User;
import lombok.Getter;
import lombok.Setter;

/**
 * @Program: tmall_springboot
 * @Description:
 * @Author: YaYa
 * @Create: 2020-07-07 16:42
 */
public class MqMessage {
    @Setter@Getter
    private int goods;
    @Setter@Getter
    private User user;
}
