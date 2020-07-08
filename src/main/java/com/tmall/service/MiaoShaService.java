package com.tmall.service;

import com.tmall.dao.SeckillGoodsDAO;
import com.tmall.dao.SeckillOrderDAO;
import com.tmall.pojo.SeckillGoods;
import com.tmall.pojo.SeckillOrder;
import com.tmall.pojo.User;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Program: tmall_springboot
 * @Description: 秒杀
 * @Author: YaYa
 * @Create: 2020-07-04 22:56
 */
@Service
@Log
public class MiaoShaService {
    @Autowired
    SeckillGoodsDAO goodsDAO;
    @Autowired
    SeckillOrderDAO orderDAO;


    //查询秒杀商品
    public List<SeckillGoods> listGoods(){
        return goodsDAO.findAll();
    }

    //查询订单
    public SeckillOrder inquiry(User user,int id){
        return orderDAO.findByUserAndGoods_Id(user,id);
    }

    //查询商品
    public SeckillGoods find(int id){
        return goodsDAO.findSeckillGoodsById(id);
    }

    //减库存
    private boolean reduceStock(int goodsID){
        SeckillGoods good=goodsDAO.findSeckillGoodsById(goodsID);
        int stock=good.getStock();
        log.warning(goodsID+"库存为"+stock);
        if (stock>0){
            good.setStock(stock-1);
            goodsDAO.save(good);
            return true;
        }
        else{
            return false;
        }
    }

    //下订单   减库存  下订单
    @Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = "Exception")
    public boolean addOrder(User user, int id){
        SeckillOrder seckillOrder=new SeckillOrder();
        SeckillGoods goods;
        if (reduceStock(id)){
            goods=goodsDAO.findSeckillGoodsById(id);
        }else {
            log.warning("减库存失败");
            return false;
        }
        seckillOrder.setUser(user);
        seckillOrder.setGoods(goods);
        orderDAO.save(seckillOrder);
        log.info("保存成功");
        return true;
    }


}
