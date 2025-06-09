package com.aliyun.gts.gmall.platform.trade.persistence.mapper;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * Created by auto-generated on 2021/03/23.
 */

@Mapper
public interface TcOrderOperateFlowMapper extends BaseMapper<TcOrderOperateFlowDO> {


    @Insert("<script>" +
            "INSERT INTO tc_order_operate_flow(primary_order_id,order_id,from_order_status,to_order_status,operator_type,op_name,operator,gmt_create,gmt_modified,features)VALUES\n" +
            "<foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\",\">\n" +
            "\t(#{item.primaryOrderId},#{item.orderId},#{item.fromOrderStatus},#{item.toOrderStatus},#{item.operatorType},#{item.opName},#{item.operator},now(),now(),#{item.features})\n" +
            "</foreach>" +
            "</script>")
    int batchCreate(@Param("list") List<TcOrderOperateFlowDO> tcOrderOperateFlowDOS);

}
