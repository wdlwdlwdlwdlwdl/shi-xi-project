package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "商品特性返回对象")
public class ItemCharacteristicsVO extends AbstractOutputInfo {


    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "明细")
    private List<ItemCharacteristicsDetailVO> list;
}
