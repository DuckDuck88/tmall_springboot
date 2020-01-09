package com.tmall.conparator;

import com.tmall.pojo.Product;

import java.util.Comparator;

/*
 * 用来排序的比较器，在展示页面对商品排序
 * 这个是评价数量排序
 * */
public class ProductReviewComparator implements Comparator<Product> {

    @Override
    public int compare(Product p1, Product p2) {
        return p2.getReviewCount()-p1.getReviewCount();
    }
}
