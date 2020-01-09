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
import java.util.Date;

@Entity
@Table(name="review")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    @Getter@Setter
    private int id;

    @ManyToOne
    @JoinColumn(name="uid")
    @Getter@Setter
    private User user;

    @ManyToOne
    @JoinColumn(name="pid")
    @Getter@Setter
    private Product product;

    @Getter@Setter
    private String content;
    @Getter@Setter
    private Date createDate;

}
