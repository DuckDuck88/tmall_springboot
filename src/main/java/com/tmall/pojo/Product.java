package com.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.elasticsearch.annotations.Document;

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
@Table(name="product")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
@Document(indexName = "tmall_springboot",type = "product") //elasticsearch全局搜索注解
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    @Getter@Setter
    private int id;

    @ManyToOne
    @JoinColumn(name="cid")
    @Getter@Setter
    @NotFound(action= NotFoundAction.IGNORE)
    private Category category;

    //没有Column到哪个属性时，则自动关联同名字段
    @Getter@Setter
    private String name;
    @Getter@Setter
    private String subTitle;
    @Getter@Setter
    private float originalPrice;
    @Getter@Setter
    private float promotePrice;
    @Getter@Setter
    private int stock;
    @Getter@Setter
    private Date createDate;

    @Transient
    @Getter@Setter
    private ProductImage firstProductImage;
    @Transient
    @Getter@Setter
    private List<ProductImage> productSingleImages; //单个产品图片
    @Transient
    @Getter@Setter
    private List<ProductImage> productDetailImages; //详情图片
    @Transient
    @Getter@Setter
    private int saleCount; //销量
    @Transient
    @Getter@Setter
    private int reviewCount; //累计评价
}
