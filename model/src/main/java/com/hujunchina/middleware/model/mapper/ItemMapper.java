package com.hujunchina.middleware.model.mapper;

import com.hujunchina.middleware.model.entity.Item;
import org.apache.ibatis.annotations.Param;

public interface ItemMapper {
//  CRUD
    int deleteByPrimaryKey(Integer id);
    int insert(Item record);
    int insertSelective(Item record);
    int updateByPrimaryKey(Item record);
    int updateByPrimaryKeySelective(Item record);
    Item selectByPrimaryKey(Integer id);
    Item selectByCode(@Param("code") String code);
}
