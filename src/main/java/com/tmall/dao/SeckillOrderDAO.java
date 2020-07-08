package com.tmall.dao;

import com.tmall.pojo.SeckillOrder;
import com.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName SeckillOrderDAO
 * @Description TODO
 * @Author 是哪的鸭
 * @Date
 * @Version 1.0
 */
public interface SeckillOrderDAO extends JpaRepository<SeckillOrder,Integer> {
    SeckillOrder findAllByUser_Id(int uid);
    SeckillOrder findAllByGoods_Id(int gid);
    SeckillOrder findByUserAndGoods_Id(User user,int id);
}
