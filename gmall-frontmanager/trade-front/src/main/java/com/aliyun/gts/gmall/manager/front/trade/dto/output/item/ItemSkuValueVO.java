package com.aliyun.gts.gmall.manager.front.trade.dto.output.item;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品SKU属性值
 *
 * @author tiansong
 */
@Data
@ApiModel("商品SKU属性值")
public class ItemSkuValueVO extends AbstractOutputInfo {
    @ApiModelProperty("属性值ID")
    private String vid;
    @ApiModelProperty("属性值")
    private String value;
    @ApiModelProperty("属性值URL")
    private String picUrl;
}
