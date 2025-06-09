package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;
import java.util.Map;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.SkuVO;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.ItemSaleSellerTempVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: ItemDetailV2VO.java
 * @Description: 商品详情返回对象
 * @author zhao.qi
 * @date 2024年10月29日 16:39:33
 * @version V1.0
 */
@Getter
@Setter
public class ItemDetailV2VO extends AbstractOutputInfo {
    static final long serialVersionUID = 1L;
    /** 商品id */
    private Long id;

    /** 商品信息 */
    private ItemVO item;
   
    /** sku信息,通过option选择 */
    private List<SkuVO> skuList;

    /** 商品规格 */
    private List<ItemProductOptionVO> productOptions;

    /** 商品规格sku */
    private Map<String, Long> skuPropSkuMap;

    private ItemSkuPriceVO itemSkuPrice;

    private List<ItemSaleSellerTempVO> saleSellerList;

    /** 商品类目属性组 */
    private List<ItemCatPropGroupVO> itemCatPropGroups;

    /** 活动详情 */
    private PromotionDetailVO promotionDetail;

    /** 秒杀活动:true 达到购买上线,禁止购买,false可以购买 */
    private boolean disableBuyCamp = false;

    /** 单次限购数量;交易根据此字段对比商品限购取最小值校验 校验0表示禁止止下单 */
    private Integer buyLimitNum;

    /** 限制下单次数;交易记录在本活动下该商品下单次数,超出后禁止再下单 校验0表示禁止止下单 */
    private Integer buyLimitOrds;

    /** 分期数量 */
    private List<Integer> intallmentsPeriodTypes;
}
