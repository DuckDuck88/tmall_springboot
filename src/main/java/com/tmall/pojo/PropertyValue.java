package com.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="propertyvalue")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
@ToString
public class PropertyValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter@Getter
    private int id;

    @ManyToOne
    @JoinColumn(name="ptid")
    @Setter@Getter
    @NotFound(action= NotFoundAction.IGNORE)
    private Property property;

    @ManyToOne
    @JoinColumn(name = "pid")
    @Setter@Getter
    private Product product;
    @Setter@Getter
    private String value;
}
