package com.aliyun.gts.gmall.platform.trade.persistence.mapper;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderExtendDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcSellerConfigDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;


/**
 * Created by auto-generated on 2021/03/19.
 */

@Mapper
public interface TcOrderExtendMapper extends BaseMapper<TcOrderExtendDO> {


    @Insert("INSERT INTO `tc_order_extend` (" +
            "   `primary_order_id`,`order_id`,`cust_id`,`extend_type`,`extend_key`,`extend_value`,`extend_name`,`valid`,`gmt_create`,`gmt_modified`" +
            ") VALUES (" +
            "   #{primaryOrderId},#{orderId},#{custId},#{extendType},#{extendKey},#{extendValue},#{extendName},#{valid},now(),now()" +
            ") ON DUPLICATE KEY UPDATE " +
            "   extend_value=#{extendValue}, extend_name=#{extendName}, valid=#{valid}, gmt_modified=now()")
    boolean insertOrUpdate(TcOrderExtendDO ext);
}
