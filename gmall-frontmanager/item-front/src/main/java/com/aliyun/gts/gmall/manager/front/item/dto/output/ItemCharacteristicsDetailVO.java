package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "商品特性详情返回对象")
public class ItemCharacteristicsDetailVO extends AbstractOutputInfo {


    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "值")
    private String value;
}
