package com.aliyun.gts.gmall.platform.trade.persistence.mapper;

import com.aliyun.gts.gmall.platform.trade.domain.entity.common.ShowNodeDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DbTableMapper extends BaseMapper<Map> {

    /**
     * 查询所有分库
     */
    @Select("show node")
    List<ShowNodeDO> showNode();

    /**
     * 查询所有分表
     */
    @Select("/*TDDL:node(${dbName})*/ SHOW TABLES")
    List<String> selectTable(@Param("dbName") String dbName);
}
