package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.framework.domain.extend.service.DomainExtendService;
import com.aliyun.gts.gmall.framework.domain.extend.strategy.ExtendStoreTypeEnum;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderFeatureKeyConstants;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderFeatureExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerAccountInfo;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.collections.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DefaultOrderFeatureExt implements OrderFeatureExt {

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private DomainExtendService domainExtendService;

    @Override
    public Map<String, String> getFeatureOnCrete(MainOrder mainOrder, CreatingOrder creatingOrder) {
//        SellerAccountInfo sellerAccountInfo = checkAndGetNonNullAccount(mainOrder);
//        Map<String, String> map = Maps.newHashMap();
//        if (sellerAccountInfo != null) {
//            map.put(OrderFeatureKeyConstants.SELLER_ACCOUNT_INFO,
//                DomainExtendService.objectToDbString(sellerAccountInfo, ExtendStoreTypeEnum.JSON)
//            );
//        }
//        return map;
        return  new HashMap<>();
    }

    private SellerAccountInfo checkAndGetNonNullAccount(MainOrder mainOrder) {
        SellerAccountInfo sellerAccountInfo = mainOrder.getSeller().getSellerAccountInfo();
        if (sellerAccountInfo == null) {
            return null;
        }
        if (StringUtils.isEmpty(sellerAccountInfo.getAlipayAccountNo())
                && StringUtils.isEmpty(sellerAccountInfo.getWechatAccountNo())) {
            return null;
        }

        return sellerAccountInfo;
    }

    @Override
    public Map<String, String> getSubFeatureOnCrete(SubOrder subOrder, MainOrder mainOrder, CreatingOrder creatingOrder) {
        return null;
    }

    @Override
    public List<String> getTagsOnCrete(MainOrder mainOrder, CreatingOrder creatingOrder) {
        return null;
    }

    @Override
    public List<String> getSubTagsOnCrete(SubOrder subOrder, MainOrder mainOrder, CreatingOrder creatingOrder) {
        return null;
    }

    @Override
    public void addSubItemStoredMap(List<MainOrder> mainOrders) {

    }
}
