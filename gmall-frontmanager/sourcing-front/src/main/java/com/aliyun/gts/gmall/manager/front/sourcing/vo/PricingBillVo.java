package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PricingBillDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/19 20:00
 */
@Data
public class PricingBillVo implements Serializable {
    /**
     * 比价单信息
     */
    private PricingBillDTO billInfo;
    /**
     * 询价单信息
     */
    private SourcingVo sourcingInfo;
    /**
     * 供应商信息
     */
    private Collection<SupplierVo> suppliers;

    /**
     * 每个物料的报价
     */
    private List<MaterialQuoteVo> materialQuotes;
    /**
     * 单个用户报价的总信息
     */
    private List<QuoteVo> quoteVos;

    /**
     * 去下单的信息
     */
    private PricingToOrderVO toOrderInfo;


    /**
     * 内部类
     */
    @Data
    public static class MaterialQuoteVo{
        /**
         * 物料信息
         */
        private SourcingMaterialVo material;
        /**
         * 报价详情每个供应商的报价详情;
         */
        private List<QuoteDetailVo> quoteDetails;
    }
}
