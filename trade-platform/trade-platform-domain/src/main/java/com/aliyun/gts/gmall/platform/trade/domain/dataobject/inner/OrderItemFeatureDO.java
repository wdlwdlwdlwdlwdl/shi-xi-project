package com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class OrderItemFeatureDO implements Serializable {

    @ApiModelProperty("单件SKU的重量, 单位g")
    private Long weight;

    @ApiModelProperty("商品状态, ItemStatusEnum")
    private Integer itemStatus;

    @ApiModelProperty("SKU图片, 如没有SKU图片则为空")
    private String skuPic;

    @ApiModelProperty(value = "商品类型, IC字段")
    private Integer itemType;

    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    @ApiModelProperty(value = "类目ID")
    private Long categoryId;

    @ApiModelProperty(value = "类目名称")
    private String categoryName;

    @ApiModelProperty(value = "扩展存储熟悉")
    private Map<String, String> storedExt;

    @ApiModelProperty(value = "类目特征")
    private Map<String, String> categoryFeatures;//refundable 1 可退款 0不可退款

    @ApiModelProperty(value = "类目详情")
    private String categoryDetails;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;
}
