package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "创建订单返回对象")
public class CreateOrderResultDTO extends AbstractOutputInfo {

    @ApiModelProperty(value = "主订单列表")
    private List<PrimaryOrderResultDTO> orders;
}
