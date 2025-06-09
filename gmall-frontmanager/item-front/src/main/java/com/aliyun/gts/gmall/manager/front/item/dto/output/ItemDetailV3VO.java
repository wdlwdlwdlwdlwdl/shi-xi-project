package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;
import java.util.Map;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.SkuVO;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: ItemDetailV3VO.java
 * @Description: 商品详情返回对象
 * @author zhao.qi
 * @date 2024年10月29日 16:39:33
 * @version V1.0
 */
@Getter
@Setter
public class ItemDetailV3VO extends AbstractOutputInfo {
    static final long serialVersionUID = 1L;
    /** 商品id */
    private Long id;

    /** sku信息,通过option选择 */
    private List<SkuVO> skuList;

    /** 商品基础信息 */
    private ItemVO item;
    /** 商品规格 */

    private List<ItemProductOptionVO> productOptions;

    /** 商品规格sku */
    private Map<String, Long> skuPropSkuMap;

}
