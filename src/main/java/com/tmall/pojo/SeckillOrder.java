package com.tmall.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @Program: tmall_springboot
 * @Description: 秒杀订单
 * @Author: YaYa
 * @Create: 2020-07-04 23:33
 */
@Entity
@Table(name = "seckill_order")
public class SeckillOrder {
    @Id
    @Setter@Getter
    private int id;
    @OneToOne
    @JoinColumn(name = "gid")
    @Setter@Getter
    private SeckillGoods goods;
    @OneToOne
    @JoinColumn(name = "uid")
    @Setter@Getter
    private User user;
}
