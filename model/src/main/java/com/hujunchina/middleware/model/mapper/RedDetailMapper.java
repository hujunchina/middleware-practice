package com.hujunchina.middleware.model.mapper;

import com.hujunchina.middleware.model.entity.RedDetail;
import com.hujunchina.middleware.model.entity.RedDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RedDetailMapper {
    long countByExample(RedDetailExample example);

    int deleteByExample(RedDetailExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(RedDetail record);

    int insertSelective(RedDetail record);

    List<RedDetail> selectByExample(RedDetailExample example);

    RedDetail selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") RedDetail record, @Param("example") RedDetailExample example);

    int updateByExample(@Param("record") RedDetail record, @Param("example") RedDetailExample example);

    int updateByPrimaryKeySelective(RedDetail record);

    int updateByPrimaryKey(RedDetail record);
}