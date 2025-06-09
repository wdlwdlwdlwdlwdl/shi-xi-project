package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.aliyun.gts.gcai.platform.sourcing.common.model.BaseDTO;
import lombok.Data;


/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/6/3 16:26
 */
@Data
public class BidingPriceVo extends BaseDTO {
    private Long sourcingId;
    private Long supplierId;
    private String supplierName;
    /**
     * 税率
     */
    private Long taxRate;
    private String totalPrice;
    private Integer status;
    private Long quoteId;
    /**
     * 排名
     */
    private Integer rank;
    /**
     * 第一次报价和最后一次报价值差别
     */
    private String priceRange;
    //报价的分
    private Long totalPriceFen;
    private Long firstPriceFen;

}
