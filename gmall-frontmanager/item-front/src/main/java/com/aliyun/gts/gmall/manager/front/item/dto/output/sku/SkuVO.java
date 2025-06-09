package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import java.util.List;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.framework.i18n.MultiLangText;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkuVO extends AbstractOutputInfo {
    private static final long serialVersionUID = 1L;

    /** skuId */
    private Long id;

    /** 商品id */
    private Long itemId;

    /** 系统默认生成code */
    private String code;

    /** 名称 */
    private MultiLangText name;

    /** 1:启用,0:禁用 */
    private Integer status;

    /** sku属性信息 */
    private List<SkuPropVO> skuPropList;

    /** 图片地址 */
    private List<String> pictureList;
}
