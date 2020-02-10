package com.tmall.service;

import com.tmall.dao.PropertyValueDAO;
import com.tmall.pojo.Product;
import com.tmall.pojo.Property;
import com.tmall.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "propertyValues")
public class PropertyValueService {
    @Autowired
    PropertyValueDAO propertyValueDAO;
    @Autowired
    PropertyService propertyService;

    //改
    @CacheEvict(allEntries = true)
    public void update(PropertyValue bean){
        propertyValueDAO.save(bean);
    }
    //通过属性名和商品名确定属性值
    @Cacheable(key = "'propertyValues-one-pid'+#p0.id+'-ptid-'+p1.id")
    public PropertyValue getByPropertyAndProduct(Product product, Property property){
        return propertyValueDAO.getByPropertyAndProduct(property, product);
    }

    //获取某个商品的全部属性
    @Cacheable(key = "'propertyValues-pid-'+#p0.id")
    public List<PropertyValue> list(Product product){
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }

    //新建属性时初始化属性值
    public void init(Product product){
        List<Property> properties=propertyService.listByCategory(product.getCategory());
        for (Property property:properties){
            PropertyValue propertyValue=getByPropertyAndProduct(product, property);
            if (null==propertyValue){
                propertyValue=new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDAO.save(propertyValue);
            }
        }
    }

}
