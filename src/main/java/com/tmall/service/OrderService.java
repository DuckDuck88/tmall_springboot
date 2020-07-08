package com.tmall.service;

import com.tmall.dao.OrderDAO;
import com.tmall.pojo.Order;
import com.tmall.pojo.OrderItem;
import com.tmall.pojo.User;
import com.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    //订单状态
    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";

    @Autowired
    OrderDAO orderDAO;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;

    public Page4Navigator<Order> list(int start,int size,int navigatePages){
        Sort sort=Sort.by("id");
        Pageable pageable=PageRequest.of(start,size,sort);
        Page pageFromJPA=orderDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    public void removeOrderFromOrderItem(List<Order> orders){
        for (Order order:orders){
            removeOrderFromOrderItem(order);
        }
    }

    public void removeOrderFromOrderItem(Order order){
        List<OrderItem> orderItems=order.getOrderItems();
        for (OrderItem orderItem:orderItems){
            orderItem.setOrder(null);
        }
    }

    public Order get(int oid){
        return orderDAO.findById(oid).get();
    }

    public void update(Order bean){
        orderDAO.save(bean);
    }

    //以事务的方式提交订单，@transactional为事务注解
    @Transactional(propagation = Propagation.REQUIRED,rollbackForClassName = "Exception")
    public float add(Order order,List<OrderItem> ois){
        float total =0;
        add(order);
//        if (false){ //当为true时故意抛出异常，测试事务
//            throw new RuntimeException();
//        }
        for (OrderItem oi: ois) {
            oi.setOrder(order);
            orderItemService.update(oi);
            total+=oi.getProduct().getPromotePrice()*oi.getNumber();
        }
        return total;
    }
    public void add(Order order) {
        orderDAO.save(order);
    }

    public List<Order> listByUserWithoutDelete(User user) {
        List<Order> orders = listByUserAndNotDeleted(user);
        orderItemService.fill(orders);
        return orders;
    }

    public List<Order> listByUserAndNotDeleted(User user) {
        return orderDAO.findByUserAndStatusNotOrderByIdDesc(user, OrderService.delete);
    }
    //计算订单
    public void cacl(Order o){
        List<OrderItem> orderItems=o.getOrderItems();
        float total=0;
        for (OrderItem orderItem : orderItems) {
            total+=orderItem.getProduct().getPromotePrice()*orderItem.getNumber();
        }
        o.setTotal(total);
    }
}
