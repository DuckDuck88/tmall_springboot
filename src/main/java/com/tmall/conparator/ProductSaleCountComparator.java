package com.tmall.conparator;

import com.tmall.pojo.Product;

import java.util.Comparator;

/*
 * 用来排序的比较器，在展示页面对商品排序
 * 这个是销量排序
 * */
public class ProductSaleCountComparator implements Comparator<Product> {

    @Override
    public int compare(Product p1, Product p2) {
        return p2.getSaleCount()-p1.getSaleCount();
    }
}
