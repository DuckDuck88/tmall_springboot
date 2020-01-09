package com.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tmall.service.OrderService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="order_")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    @Getter@Setter
    private int id;

    @ManyToOne
    @JoinColumn(name = "uid")
    @Getter@Setter
    private User user;

    @Getter@Setter
    private String orderCode;//订单编号
    @Setter@Getter
    private String address;//地址
    @Setter@Getter
    private String post;//价格
    @Setter@Getter
    private String receiver;//收货人
    @Setter@Getter
    private String mobile;//用户手机号
    @Setter@Getter
    private String userMessage;//用户留言
    @Setter@Getter
    private Date createDate;//订单创建时间
    @Setter@Getter
    private Date payDate; //付款时间
    @Setter@Getter
    private Date deliveryDate; //发货时间
    @Setter@Getter
    private Date confirmDate; //确定收货时间
    @Setter@Getter
    private String status;  //状态

    @Transient
    @Setter@Getter
    private List<OrderItem> orderItems;
    @Transient
    @Setter
    private String statusDesc;
    @Transient
    @Setter@Getter
    private int totalNumber;
    @Transient
    @Getter@Setter
    private float total;

    public String getStatusDesc() {
        if (null != statusDesc)
            return statusDesc;
        String desc = "未知";
        switch (status) {
            case OrderService.waitPay:
                desc = "待付";
                break;
            case OrderService.waitDelivery:
                desc = "待发";
                break;
            case OrderService.waitConfirm:
                desc = "待收";
                break;
            case OrderService.waitReview:
                desc = "等评";
                break;
            case OrderService.finish:
                desc = "完成";
                break;
            case OrderService.delete:
                desc = "刪除";
                break;
            default:
                desc = "未知";
        }
        statusDesc = desc;
        return statusDesc;
    }
}
