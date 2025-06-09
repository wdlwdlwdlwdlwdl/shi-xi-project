package com.aliyun.gts.gmall.platform.trade.persistence.mapper;

import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.MainReversalDTO;
import com.aliyun.gts.gmall.platform.trade.common.domain.KVDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatistics;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderStatisticsWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TcOrderMapper extends BaseMapper<TcOrderDO> {

    List<KVDO<Integer, Integer>> countByStatus(@Param("custId") Long custId, @Param("statusList") List<Integer> statusList);

    List<TcOrderDO> queryBoughtOrders(OrderQueryWrapper query);

    List<TcOrderDO> querySoldOrders(OrderQueryWrapper query);

    Integer countBoughtOrders(OrderQueryWrapper query);

    @Insert("<script>" + "   INSERT INTO tc_order("
            + "       `primary_order_id`,`order_id`,`primary_order_flag`,`biz_code`,`order_status`,`primary_order_status`,`order_attr`,`snapshot_path`,`order_channel`,`evaluate`,`reversal_type`,`cust_id`,`cust_name`,`cust_memo`,`item_id`,`sku_id`,`sku_desc`,`item_title`,`item_quantity`,`item_pic`,`item_feature`,`order_price`,`sale_price`,`real_price`,`order_fee_attr`,`promotion_attr`,`gmt_create`,`gmt_modified`,`receive_info`,`seller_id`,`seller_name`,`version`,`first_name`,`last_name`,`bin_or_iin`,`loan_cycle`,`display_order_id`,`pay_cart_id`,`category_commission_rate`"
            + "   ) VALUES " + "   <foreach collection='orders' item='ord' index='index' open='' close='' separator=','>" + "   ("
            + "       #{ord.primaryOrderId},#{ord.orderId},#{ord.primaryOrderFlag},#{ord.bizCode},#{ord.orderStatus},#{ord.primaryOrderStatus},#{ord.orderAttr},#{ord.snapshotPath},#{ord.orderChannel},#{ord.evaluate},#{ord.reversalType},#{ord.custId},#{ord.custName},#{ord.custMemo},#{ord.itemId},#{ord.skuId},#{ord.skuDesc},#{ord.itemTitle},#{ord.itemQuantity},#{ord.itemPic},#{ord.itemFeature},#{ord.orderPrice},#{ord.salePrice},#{ord.realPrice},#{ord.orderFeeAttr},#{ord.promotionAttr},#{ord.gmtCreate},#{ord.gmtModified},#{ord.receiveInfo},#{ord.sellerId},#{ord.sellerName},#{ord.version},#{ord.firstName},#{ord.lastName},#{ord.binOrIin},#{ord.loanCycle},#{ord.displayOrderId},#{ord.payCartId},#{ord.categoryCommissionRate}"
            + "   )" + "   </foreach>" + "</script>")
    int batchCreate(@Param("orders") List<TcOrderDO> orders);

    List<OrderStatistics> statisticsBySellerId(@Param("sellerIds") List<Long> sellerIds, @Param("orderStatus") int orderStatus);

    List<OrderStatistics> statisticsBySeller(@Param("sellerIds") List<Long> sellerIds);

    List<MainReversalDTO> statisticsSellerByCancelCodeAndTime(OrderStatisticsWrapper wrapper);

    int statisticsOrderByCancelCodeAndTime(OrderStatisticsWrapper wrapper);

    int statisticsOrderByTime(OrderStatisticsWrapper wrapper);
}
