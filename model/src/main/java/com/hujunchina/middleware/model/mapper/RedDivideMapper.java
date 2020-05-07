package com.hujunchina.middleware.model.mapper;

import com.hujunchina.middleware.model.entity.RedDivide;
import com.hujunchina.middleware.model.entity.RedDivideExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RedDivideMapper {
    long countByExample(RedDivideExample example);

    int deleteByExample(RedDivideExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(RedDivide record);

    int insertSelective(RedDivide record);

    List<RedDivide> selectByExample(RedDivideExample example);

    RedDivide selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") RedDivide record, @Param("example") RedDivideExample example);

    int updateByExample(@Param("record") RedDivide record, @Param("example") RedDivideExample example);

    int updateByPrimaryKeySelective(RedDivide record);

    int updateByPrimaryKey(RedDivide record);
}