package com.aliyun.gts.gmall.manager.front.item.adaptor;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.item.convertor.PromotionCouponConverter;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.CouponInfoReqQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.CouponInstanceVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.PromotionDetailVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.PromotionEnableSelectInfoVO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.PromotionQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.CouponInstanceDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.PromotionDetailDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.display.PromotionEnableSelectInfo;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionReadFacade;
import com.aliyun.gts.gmall.platform.promotion.common.model.TargetCust;
import com.aliyun.gts.gmall.platform.promotion.common.model.TargetItem;
import com.aliyun.gts.gmall.platform.promotion.common.model.TargetItemCluster;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class ItemPromotionAdaptor {

    @Autowired
    private PromotionReadFacade promotionReadFacade;


    @Autowired
    private PromotionCouponConverter promotionCouponConverter;

    /**
     * 查询优惠券信息
     *
     * @return PromotionInfo
     */
    public PromotionEnableSelectInfoVO queryPromotionCouponInfo(CouponInfoReqQuery query) {
        PromotionQueryReq req = getPromotionQueryReq(query);
        req.setSkuSellerIds(query.getSellerIds());
        RpcResponse<List<PromotionEnableSelectInfo>> listRpcResponse = promotionReadFacade.queryPromotionEnableSelectInfo(req);
        if (listRpcResponse == null) {
            return null;
        }
        List<PromotionEnableSelectInfo> data = listRpcResponse.getData();
        if (CollectionUtils.isEmpty(data)) {
            return null;
        }
        PromotionEnableSelectInfo promotionEnableSelectInfo = data.get(0);
        if (promotionEnableSelectInfo == null) {
            return null;
        }
        Collection<CouponInstanceDTO> coupons = promotionEnableSelectInfo.getCoupons();
        PromotionEnableSelectInfoVO promotionEnableSelectInfoVO = new PromotionEnableSelectInfoVO();
        if(CollectionUtils.isNotEmpty(coupons)){
            List<CouponInstanceVO> couponInfoVOS = new ArrayList<>();
            for (CouponInstanceDTO instanceDTO : coupons) {
                CouponInstanceVO couponInstanceVO = promotionCouponConverter.convertCouponInstance(instanceDTO);
                couponInfoVOS.add(couponInstanceVO);
            }
            promotionEnableSelectInfoVO.setCouponInstanceList(couponInfoVOS);
        }
        Collection<PromotionDetailDTO> campaigns = promotionEnableSelectInfo.getCampaigns();
        if(CollectionUtils.isNotEmpty(campaigns)){
            List<PromotionDetailVO> campaignsVOS = new ArrayList<>();
            for (PromotionDetailDTO promotionDetail : campaigns) {
                PromotionDetailVO promotionDetailVO = promotionCouponConverter.convertPromotionDetailVO(promotionDetail);
                campaignsVOS.add(promotionDetailVO);
            }
            promotionEnableSelectInfoVO.setPromotionDetailList(campaignsVOS);
        }
        return promotionEnableSelectInfoVO;
    }

    public static Long daysBetween(Date nowDate,Date endDate)  {
        long between_days= 0;
        if(nowDate == null || endDate == null){
            return between_days;
        }
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            nowDate=sdf.parse(sdf.format(nowDate));
            endDate=sdf.parse(sdf.format(endDate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(nowDate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(endDate);
            long time2 = cal.getTimeInMillis();
            between_days =(time2-time1)/(1000*3600*24);
        } catch (ParseException e) {
            return between_days;
        }
        return between_days;
    }

    private PromotionQueryReq getPromotionQueryReq(CouponInfoReqQuery query) {
        PromotionQueryReq promotionQuery = new PromotionQueryReq();
        TargetCust cust = new TargetCust();
        cust.setCustId(query.getCustId());
        promotionQuery.setCust(cust);
        List<TargetItemCluster> itemClusters = new ArrayList<>();
        TargetItemCluster targetItemCluster = new TargetItemCluster();
        List<TargetItem> targetItems = new ArrayList<>();
        TargetItem targetItem = new TargetItem();
        targetItem.setItemId(query.getItemId());
        targetItem.setSkuId(query.getSkuId());
        targetItem.setBrandId(query.getBrandId());
        targetItem.setCategoryId(query.getCategoryId());
        targetItems.add(targetItem);
        targetItemCluster.setTargetItems(targetItems);
        itemClusters.add(targetItemCluster);
        promotionQuery.setItemClusters(itemClusters);
        promotionQuery.setEnableCache(false);//启用缓存查询
        promotionQuery.setSkuSellerIds(query.getSellerIds());
        return promotionQuery;
    }
}
