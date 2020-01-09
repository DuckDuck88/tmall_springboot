package com.tmall.web;

import com.tmall.pojo.Order;
import com.tmall.service.OrderItemService;
import com.tmall.service.OrderService;
import com.tmall.util.Page4Navigator;
import com.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class OrderItemController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;

    @GetMapping(value = "orders")
    public Page4Navigator<Order> list(@RequestParam(value = "start",defaultValue = "0") int start,
                                      @RequestParam(value = "size",defaultValue = "5") int size)
            throws Exception{
        start=start>0?start:0;
        Page4Navigator<Order> page=orderService.list(start,size,5);
        orderItemService.fill(page.getContent());
        orderService.removeOrderFromOrderItem(page.getContent());
        return page;
    }

    @PutMapping(value = "deliveryOrder/{oid}")
    public Object deliveryOrder(@PathVariable int oid)throws Exception{
        Order order=orderService.get(oid);
        order.setDeliveryDate(new Date());
        order.setStatus(OrderService.waitConfirm);
        orderService.update(order);
        return Result.success();    // RESTFUL 风格返回的 json 格式
    }
}
