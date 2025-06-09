package com.aliyun.gts.gmall.manager.front.item.dto.temp;

import java.util.Objects;
import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: ItemSaleSellerQuery.java
 * @Description: 商品商家查询-入参
 * @author zhao.qi
 * @date 2024年11月13日 12:53:48
 * @version V1.0
 */
@Getter
@Setter
public class ItemSaleSellerQuery extends AbstractInputParam {
    private static final long serialVersionUID = 1L;

    // -------------------------以下是查询条件-------------------------
    private Long itemId;

    /** 商品skuId */
    private Long skuId;

    /** 城市编码 */
    private String cityCode;

    /** op商家 */
    private Boolean op;

    /** 商品关联品牌id */
    private Long brandId;

    /** 分期数 */
    private Integer period;

    /** 支付方式 **/
    private String payMode;

    /** 今日送达 */
    private Boolean deliveredToday;

    /** 明日送达 */
    private Boolean deliveredTomorrow;

    /** 过滤秒杀商家 */
    private Boolean seckillFilter;

    /** 过滤预售商家 */
    private Boolean presaleFilter;

    // -------------------------以下是排序条件-------------------------
    /** 按价格从低到高 */
    private Boolean orderByPriceAsc;

    /** 按价格从高到低 */
    private Boolean orderByPriceDesc;

    /** 按销量从高到低 */
    private Boolean orderBySalesVolumeDesc;

    /** 按商品评价数量从高到低 */
    private Boolean orderByEvaluateDesc;

    public boolean hasNoFilterCondition() {
        return Objects.isNull(op) && Objects.isNull(deliveredToday) && Objects.isNull(deliveredTomorrow);
    }

    public boolean hasNoOrderCondition() {
        return Objects.isNull(orderByPriceAsc) && Objects.isNull(orderByPriceDesc) && Objects.isNull(orderBySalesVolumeDesc)
                && Objects.isNull(orderByEvaluateDesc);
    }

    @Override
    public void checkInput() {
        ParamUtil.nonNull(this.itemId, I18NMessageUtils.getMessage("product") + " [id] " + I18NMessageUtils.getMessage("cannot.be.empty"));
        ParamUtil.nonNull(this.skuId, I18NMessageUtils.getMessage("product") + " [skuId] " + I18NMessageUtils.getMessage("cannot.be.empty"));
        ParamUtil.notBlank(this.cityCode, I18NMessageUtils.getMessage("product") + " [cityCode] " + I18NMessageUtils.getMessage("cannot.be.empty"));
        ParamUtil.nonNull(this.brandId, I18NMessageUtils.getMessage("product") + " [brandId] " + I18NMessageUtils.getMessage("cannot.be.empty"));
    }
}
