package com.aliyun.gts.gmall.platform.trade.domain.entity.promotion;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 商品营销明细
 * com.aliyun.gts.gmall.platform.promotion.common.model.PromDivideDTO
 */
@Data
public class ItemDivideDetail implements Serializable {

    /**
     * 分摊价格
     */
    private Long reduce;
    /**
     * 优惠工具
     */
    private String toolCode;
    /**
     * 活动id
     */
    private Long campId;
    /**
     *
     */
    private Long detailId;
    /**
     * 资产id
     */
    private Long assetsId;

    private Integer assetType;

    /**
     * 营销级别
     */
    private Integer level;

    /**
     * 该优惠是影响一口价的优惠
     */
    private boolean itemPrice;

    /**
     * 附加优惠结果。
     */
    private Map<String, Object> extras;

    /**
     * 活动名称
     */
    private String name;
}
