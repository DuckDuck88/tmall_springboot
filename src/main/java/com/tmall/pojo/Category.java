package com.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Entity  //实体类注解
@Table(name="category") //对应表名为category
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})   //使用前后端分离，忽略两个无需json化的属性
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter@Getter
    private int id;
    @Setter@Getter
    private String name;
    @Setter@Getter
    @Transient
    private List<Product> products; //用于前台，代表一个分类下多个产品
    @Setter@Getter
    @Transient
    private List<List<Product>> productsByRow;   //用于前台展示，当鼠标移至分类导航时，展示该分类下的推荐产品，不只一行，所以使用矩阵
}
