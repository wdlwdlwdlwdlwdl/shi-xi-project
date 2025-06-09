package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(value = "商品sku扩展返回对象")
public class ItemSkuFeaturesVO extends AbstractOutputInfo {

    @ApiModelProperty(value = "扩展名称")
    private String titleName;
    @ApiModelProperty(value = "扩展信息")
    private Map<String,String> features;

}
