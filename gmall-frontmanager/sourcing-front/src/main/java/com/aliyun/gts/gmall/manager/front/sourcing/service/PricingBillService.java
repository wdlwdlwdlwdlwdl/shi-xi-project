package com.aliyun.gts.gmall.manager.front.sourcing.service;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PricingBillDTO;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.sourcing.input.PricingBillReq;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.PricingBillVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.PricingToOrderVO;

import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/19 19:26
 */
public interface PricingBillService {
    /**
     * 报价单ID
     * @param billId
     * @return
     */
    PricingBillVo queryBillById(Long billId);
    /**
     * 寻源id合报价ID
     * @param sourcingId
     * @param quoteId
     * @return
     */
    PricingBillVo queryBill(Long sourcingId, List<Long> quoteId);

    /**
     * 创建比价单
     * @return
     */
    RestResponse<Boolean> createBillAndUpdate(PricingBillDTO billDTO);

    /**
     * 创建比价单
     * @return
     */
    RestResponse<Boolean> updateBill(PricingBillDTO billDTO);

    // 决标拆分下单信息
    void fillToOrder(PricingBillVo bill, PricingBillReq req);
}
