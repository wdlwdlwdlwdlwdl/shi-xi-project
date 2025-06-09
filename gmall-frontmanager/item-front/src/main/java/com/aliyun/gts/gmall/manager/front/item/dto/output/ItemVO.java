package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: ItemVO.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2025年3月9日 13:01:43
 * @version V1.0
 */
@Getter
@Setter
public class ItemVO extends AbstractOutputInfo {
    private static final long serialVersionUID = 1L;

    /** 商品id */
    private Long id;

    private Integer itemType;

    /** 商品标题 */
    private String title;

    /** 商品图片,已按order排好序 */
    private List<String> pictureList;

    /** 商品类目id */
    private Long categoryId;

    /** 品牌id */
    private Long brandId;

    /** 品牌名 */
    private String brandName;

    /** 品牌logo */
    private String brandLogo;

    /** 商品描述,oss地址 */
    private String itemDesc;

    /** 是否超过21岁 */
    private Integer age21;
}
