package com.aliyun.gts.gmall.platform.trade.api.dto.output.order;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CheckOutOrderResultDTO extends AbstractOutputInfo {

    // 校验成功
    private Boolean checkSuccess;

    // 已经通过
    private Boolean createdSuccess;

    @ApiModelProperty("临时订单")
    private List<Long> originMainOrderList;
    
}
