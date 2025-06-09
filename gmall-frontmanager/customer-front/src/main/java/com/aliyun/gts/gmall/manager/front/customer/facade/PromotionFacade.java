package com.aliyun.gts.gmall.manager.front.customer.facade;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.ApplyCouponRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.BatchApplyCouponRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.*;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.*;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookFailureDTO;

/**
 * 券服务
 *
 * @author GTS
 * @date 2021/03/12
 */
public interface PromotionFacade {

    /**
     * 领券接口
     *
     * @param command
     * @return
     */
    RestResponse<CouponInstanceVO> applyCoupon(ApplyCouponRestCommand command);

    /**
     * 根据code查询券
     *
     * @param query
     * @return
     */
    CouponInstanceVO queryCoupon(ByCodeCouponQuery query);


    /**
     * 根据code查询券
     *
     * @param query
     * @return
     */
    CouponInstanceDetailVO queryCouponDetail(ByCodeCouponQuery query);

    /**
     * 查询买家已领到的券
     *
     * @param query
     * @return
     */
    RestResponse<PageInfo<CouponInstanceVO>> queryCoupons(CustomerCouponQuery query);

    /**
     * 查询当前用户积分
     *
     * @param query
     */
    RestResponse<Long> queryAccountBook(AccountBookRestQuery query);

    /**
     * 查询积分记录
     *
     * @param query
     * @return
     */
    RestResponse<PageInfo<AcBookRecordVO>> queryAcBookRecords(AccountBookRecordRestQuery query);

    RestResponse<PromCampaignVO> queryCouponDetail(ByIdCouponQuery query);

    RestResponse<AcBookFailureDTO> getAccountBookFailure(AccountBookRestQuery query);
    RestResponse<CouponInstanceReceiveVO> queryNewUserCoupons(CouponAndCampaignQuery query);

    RestResponse<CouponBatchApplyRetVO> batchApplyCoupon(BatchApplyCouponRestCommand command);
}