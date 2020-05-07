package com.hujunchina.middleware.model.mapper;

import com.hujunchina.middleware.model.entity.RedRecord;
import com.hujunchina.middleware.model.entity.RedRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RedRecordMapper {
    long countByExample(RedRecordExample example);

    int deleteByExample(RedRecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(RedRecord record);

    int insertSelective(RedRecord record);

    List<RedRecord> selectByExample(RedRecordExample example);

    RedRecord selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") RedRecord record, @Param("example") RedRecordExample example);

    int updateByExample(@Param("record") RedRecord record, @Param("example") RedRecordExample example);

    int updateByPrimaryKeySelective(RedRecord record);

    int updateByPrimaryKey(RedRecord record);
}