package com.aliyun.gts.gmall.manager.front.customer.adaptor;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.CouponCampQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.CouponQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.CouponInstanceDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionCouponFacade;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromDisplayConstants;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionToolCodes;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.collections.Lists;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.PROMOTION_CENTER_ERROR;

/**
 * 营销接口
 *
 * @author tiansong
 */
@Slf4j
@ApiOperation(value = "营销接口")
@Component
public class PromotionAdapter {
    @Autowired
    private PromotionCouponFacade promotionCouponFacade;
    @Autowired
    private DatasourceConfig      datasourceConfig;

    private final DubboBuilder builder = DubboBuilder.builder().logger(log).sysCode(PROMOTION_CENTER_ERROR).build();

    /**
     * 根据活动ID获取活动信息
     *
     * @param couponIds
     * @return
     */
    public List<CouponInstanceDTO> queryCouponByIds(Set<Long> couponIds) {
        CouponCampQueryReq couponCampQueryReq = new CouponCampQueryReq();
        couponCampQueryReq.setCampaignIds(Lists.newArrayList(couponIds));
        couponCampQueryReq.setPage(new PageParam(1, couponIds.size()));
        couponCampQueryReq.setPromStatus(13);
        List<CouponInstanceDTO> results = builder.create(datasourceConfig)
                .id(DsIdConst.customer_coupon_batchQuery)
                .queryFunc((Function<CouponCampQueryReq, RpcResponse<List<CouponInstanceDTO>>>) request -> promotionCouponFacade.queryCouponCampaign(request))
                .strong(Boolean.FALSE)
                .query(couponCampQueryReq);
//        if (CollectionUtils.isNotEmpty(results)) {
//            for (CouponInstanceDTO result : results) {
//                String promotionToolCode = result.getPromotionToolCode();
//                if (PromotionToolCodes.MANZHE.equals(promotionToolCode)) {
//                    JSONObject display = result.getDisplay();
//                    String value = String.valueOf(display.get(PromDisplayConstants.DISCOUNT));
//                    if (StringUtils.isNotBlank(value)) {
//                        BigDecimal bigDecimal = new BigDecimal(value);
//                        display.put(PromDisplayConstants.DISCOUNT, BigDecimal.valueOf(100L).subtract(bigDecimal));
//                    }
//                }
//            }
//        }

        return results;
    }

    /**
     * 查询用户领取的券信息
     *
     * @param custId
     * @param couponIds
     * @return
     */
    public PageInfo<CouponInstanceDTO> queryCustomerCoupon(Long custId, Set<Long> couponIds) {
        CouponQueryReq couponQueryReq = new CouponQueryReq();
        couponQueryReq.setCustId(custId);
        couponQueryReq.setCampaignIds(Lists.newArrayList(couponIds));
        // 设置最近30天
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DATE, -30);
        couponQueryReq.setApplyTimeStart(calendar.getTime());
        couponQueryReq.setPage(new PageParam(1, couponIds.size()));
        return builder.create(datasourceConfig)
            .id(DsIdConst.customer_coupon_pageQuery)
            .queryFunc((Function<CouponQueryReq, RpcResponse<PageInfo<CouponInstanceDTO>>>) request -> promotionCouponFacade.page(request))
            .strong(Boolean.FALSE)
            .query(couponQueryReq);
    }
}
