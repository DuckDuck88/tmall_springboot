package com.tmall.dao;

import com.tmall.pojo.SeckillGoods;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName SeckillGoodsDAO
 * @Description TODO
 * @Author 是哪的鸭
 * @Date
 * @Version 1.0
 */
public interface SeckillGoodsDAO extends JpaRepository<SeckillGoods,Integer> {
    SeckillGoods findSeckillGoodsById(int id);
    SeckillGoods findSeckillGoodsByName(String name);
}
