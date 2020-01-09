package com.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

@Entity
@Table(name="orderitem")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class OrderItem {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    @Getter@Setter
    private int id;

    @ManyToOne
    @JoinColumn(name="pid")
    @Setter@Getter
    private Product product;

    @ManyToOne
    @JoinColumn(name="oid")
    @Getter@Setter
    private Order order;

    @ManyToOne
    @JoinColumn(name="uid")
    @Getter@Setter
    private User user;

    @Setter@Getter
    private int number;
}
