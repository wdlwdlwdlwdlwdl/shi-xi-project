package com.aliyun.gts.gmall.manager.front.login.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplyQueryResult extends AbstractOutputInfo {

    @ApiModelProperty("入驻信息")
    private ApplyInfo applyInfo;

    @ApiModelProperty("用户状态")
    private String customerStatus;

    @ApiModelProperty("审核意见")
    private String approveMessage;
}
