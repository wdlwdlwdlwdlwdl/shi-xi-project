package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create;

import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@ApiModel
public class OrderGroupInfo extends AbstractInputParam {

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("订单备注")
    private String remark;

    @ApiModelProperty(value = "订单扩展属性, 存储总长限制4k, 接口自定义的不要超过1k")
    private Map<String, String> extraFeature = new HashMap<>();

    @ApiModelProperty(value = "订单扩展结构, 每个value限制2k")
    private Map<String /*type*/,
            Map<String /*key*/, String /*value*/>> expendStruct = new HashMap<>();
}
