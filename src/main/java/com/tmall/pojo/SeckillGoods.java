package com.tmall.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Program: tmall_springboot
 * @Description: 秒杀商品
 * @Author: YaYa
 * @Create: 2020-07-04 22:22
 */
@Entity
@Table(name = "seckill_goods")
public class SeckillGoods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter@Setter
    private long id;
    @Getter@Setter
    private String name;
    @Getter@Setter
    private int stock;
    @Getter@Setter
    private double price;
    @Getter@Setter
    private Date starttime;
    @Getter@Setter
    private Date endtime;
}
