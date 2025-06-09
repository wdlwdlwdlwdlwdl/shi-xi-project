package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

import java.util.Map;

/**
 * 商品分摊营销活动明细
 */
@Data
public class ItemDividePromotionDTO extends AbstractOutputInfo {

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
