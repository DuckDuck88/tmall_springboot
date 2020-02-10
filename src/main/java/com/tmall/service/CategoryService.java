package com.tmall.service;

import com.tmall.dao.CategoryDAO;
import com.tmall.pojo.Category;
import com.tmall.pojo.Product;
import com.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * // @CachePut(key="'category-one-'+ #p0")
 * 它的作用是以 category-one-id 的方式增加到 Redis中去。
 * 这样做本身其实是没有问题的，而且在 get 的时候，还可以使用
 * 但是： 它并不能更新分页缓存 categories-page-0-5 里的数据， 这样就会出现数据不一致的问题了。
 * 即。在redis 中，有这一条单独数据的缓存，但是在分页数据里，却没有这一条，这样就矛盾了。
 * 为了解决这个矛盾，最精细的做法是，同时更新分页缓存里的数据。 因为 redis 不是结构化的数据，它是 “nosql"
 * 为了做到同时更新缓存分页缓存里的数据，会超级的复杂，而且超级容易出错，其开发量也会非常大。
 * 采纳了折中的办法，
 * 即，一旦增加了某个分类数据，那么就把缓存里所有分类相关的数据都清除掉。
 * 下一次再访问的时候，一看，缓存里没数据，那么就会从数据库中读出来，读出来之后，再放在缓存里。
 * 牺牲了一点小小的性能，数据的一致性就得到了保障
 */
@Service
@CacheConfig(cacheNames = "categories")//表示分类在缓存里的keys，都是归 "categories" 这个管理的
public class CategoryService {
    @Autowired
    CategoryDAO categoryDAO;

    //分页函数
    @Cacheable(key = "'categories-page-'+#p0+'-'+#p1")
    public Page4Navigator<Category> list(int start,int size,int navigatePages){
        Pageable pageable = PageRequest.of(start, size, Sort.Direction.ASC,"id");
        Page pageFromJPA =categoryDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    //查询列表
    @Cacheable(key = "'categories-all'")
    public List<Category> list() {
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        return categoryDAO.findAll(Sort.by(order));
    }

    //增加
    @CacheEvict(allEntries = true)
//    @CachePut(key = "'categories-one-'+#p0")
    public void add(Category bean){
        categoryDAO.save(bean);
    }

    //删除
    @CacheEvict(allEntries = true)
//    @CacheEvict(key = "'categories-one-'+#p0")
    public void delete(int id){
        categoryDAO.deleteById(id);
    }

    //获取
    @Cacheable(key = "'categories-one-'+#p0")
    public Category get(int id){
        Category category=categoryDAO.findById(id).get();
        return category;
    }

    //更新数据
//    @CachePut(key = "'categories-one-'+#p0")
    @CacheEvict(allEntries = true)
    public void update(Category bean){
        categoryDAO.save(bean);
    }


    //去掉product里面的分类属性。转化为json时会遍历product，product里包含category属性，无限循环
    //增加此方法后，无法实现从产品中获取分类属性的业务
    public void removeCategoryFromProduct(List<Category> categories){
        for (Category category:categories){
            removeCategoryFromProduct(category);
        }
    }
    public void removeCategoryFromProduct(Category category){
        List<Product> products=category.getProducts();
        if(null!=products){
            for (Product product:products){
                product.setCategory(null);
            }
        }
        List<List<Product>> productsByRow = category.getProductsByRow();
        if(null!=productsByRow){
            for (List<Product> product:productsByRow){
                for (Product p:product){
                    p.setCategory(null);
                }
            }
        }
    }

}
