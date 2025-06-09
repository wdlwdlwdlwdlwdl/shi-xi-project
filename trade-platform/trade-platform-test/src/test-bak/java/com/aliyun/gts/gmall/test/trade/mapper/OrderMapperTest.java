package com.aliyun.gts.gmall.test.platform.mapper;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.PrimaryOrderFlagEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.domain.KVDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderFeeAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderItemFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.PromotionAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcOrderMapper;
import com.aliyun.gts.gmall.test.platform.base.BaseTest;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.collections.Lists;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class OrderMapperTest extends BaseTest {

    @Autowired
    TcOrderMapper tcOrderMapper;

    @Test
    @Ignore
    public void testInsert(){
        DecimalFormat format = new DecimalFormat();
        PodamFactory factory = new PodamFactoryImpl();
        TcOrderDO tcOrderDO = new TcOrderDO();
        tcOrderDO.setPrimaryOrderId(1111111111L);
        tcOrderDO.setBizCode(Lists.newArrayList("a","b"));
        tcOrderDO.setCustId(1111L);
        tcOrderDO.setCustMemo("custmemo");
        tcOrderDO.setItemFeature(new OrderItemFeatureDO());
        tcOrderDO.setItemId(111111L);
        tcOrderDO.setItemPic("http://xxx");
        tcOrderDO.setItemQuantity(1);
        tcOrderDO.setItemTitle("itemtitle");
        OrderAttrDO orderAttrDO = new OrderAttrDO();
        //orderAttrDO.setTest("test");
        tcOrderDO.setOrderAttr(orderAttrDO);
        OrderFeeAttrDO orderFeeAttrDO = new OrderFeeAttrDO();
        //orderFeeAttrDO.setTest("test");
        tcOrderDO.setOrderFeeAttr(orderFeeAttrDO);
        PromotionAttrDO promotionAttrDO = new PromotionAttrDO();
        //promotionAttrDO.setTest("test");
        tcOrderDO.setPromotionAttr(promotionAttrDO);
        tcOrderDO.setOrderChannel("channel");
        tcOrderDO.setOrderId(1111111L);
        tcOrderDO.setOrderPrice(11L);
        tcOrderDO.setOrderStatus(OrderStatusEnum.ORDER_BUYER_CANCEL.getCode());
        tcOrderDO.setPrimaryOrderFlag(PrimaryOrderFlagEnum.PRIMARY_ORDER.getCode());
        tcOrderDO.setPrimaryOrderStatus(OrderStatusEnum.ORDER_BUYER_CANCEL.getCode());
        tcOrderDO.setRealPrice(1L);
        //tcOrderDO.setReceiveInfo("xxxx");
        tcOrderDO.setReversalType(ReversalTypeEnum.REFUND_ITEM.getCode());
        tcOrderDO.setSalePrice(22L);
        tcOrderDO.setSellerId(112222L);
        tcOrderDO.setSellerName("seller");
        tcOrderDO.setSkuDesc("sku");
        tcOrderDO.setSkuId(1111L);
        tcOrderDO.setSnapshotPath("snap");
        tcOrderDO.setCustName("buyer");
        tcOrderDO.setGmtCreate(new Date());
        tcOrderDO.setGmtModified(new Date());
        tcOrderMapper.insert(tcOrderDO);

    }

    @Test
    public void testUpdate(){
        TcOrderDO tcOrderDO = new TcOrderDO();
        tcOrderDO.setId(100125L);
        tcOrderDO.setPrimaryOrderId(1111111111L);
        tcOrderDO.setOrderId(1111111L);
        PromotionAttrDO promotionAttrDO = new PromotionAttrDO();
        //promotionAttrDO.setTest("hahahaha");
        tcOrderDO.setPromotionAttr(promotionAttrDO);
        tcOrderDO.setOrderStatus(OrderStatusEnum.ORDER_CONFIRM.getCode());
        LambdaUpdateWrapper<TcOrderDO> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(TcOrderDO::getPrimaryOrderId,1111111111L);
        wrapper.eq(TcOrderDO::getOrderId,1111111L);
        wrapper.eq(TcOrderDO::getId,100125L);
        tcOrderMapper.update(tcOrderDO , wrapper);
    }

    @Test
    @Ignore
    public void testQuery(){
        TcOrderDO tcOrderDO = tcOrderMapper.selectById(100125L);
        System.out.println(tcOrderDO.getOrderStatus());
    }

    @Test
    @Ignore
    public void testCount(){
        //QueryWrapper wrapper = new QueryWrapper();
        //wrapper.select("order_status,count(*) as total").
        //    in(true,"order_status",Lists.newArrayList(25,10)).
        //    eq(true,"cust_id",401L).groupBy("order_status");
        //
        //List<Map<String, Object>> map = tcOrderMapper.selectMaps(wrapper);
        //List list = tcOrderMapper.selectObjs(wrapper);
        //System.out.println(map);

        List<KVDO<Integer , Integer>> list = tcOrderMapper.countByStatus(401L, Lists.newArrayList(12,10));
        System.out.println(list);
    }

    @Test
    @Ignore
    public void testBought(){
        OrderQueryWrapper query = new OrderQueryWrapper();
        query.setCustId(100198L);
        query.setPrimaryOrderFlag(true);
        List<TcOrderDO> list = tcOrderMapper.queryBoughtOrders(query);
        System.out.println(list.size());
    }

    @Autowired
    TcOrderRepository tcOrderRepository;
    @Test
    @Ignore
    public void testUpdateVersion(){
        TcOrderDO tcOrderDO = tcOrderMapper.selectById(30000003107170L);
        System.out.println(tcOrderDO.getOrderAttr().getPayChannel());
        OrderAttrDO orderAttrDO = tcOrderDO.getOrderAttr();
        orderAttrDO.setPayChannel("101");
        TcOrderDO update = new TcOrderDO();
        update.setPrimaryOrderId(tcOrderDO.getPrimaryOrderId());
        update.setOrderId(tcOrderDO.getPrimaryOrderId());
        update.setVersion(tcOrderDO.getVersion());
        update.setOrderAttr(orderAttrDO);
        tcOrderRepository.updateByOrderIdVersion(update);
    }


}
