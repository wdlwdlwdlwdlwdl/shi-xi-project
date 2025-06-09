package com.aliyun.gts.gmall.platform.trade.persistence.mapper;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcSellerConfigDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

public interface TcSellerConfigMapper extends BaseMapper<TcSellerConfigDO> {

    @Insert("insert into tc_seller_config(seller_id, conf_name, conf_value, gmt_create, gmt_modified) " +
            "values (#{sellerId}, #{confName}, #{confValue}, now(), now()) " +
            "ON DUPLICATE KEY UPDATE conf_value=#{confValue}, gmt_modified=now()")
    boolean insertOrUpdate(TcSellerConfigDO conf);
}
