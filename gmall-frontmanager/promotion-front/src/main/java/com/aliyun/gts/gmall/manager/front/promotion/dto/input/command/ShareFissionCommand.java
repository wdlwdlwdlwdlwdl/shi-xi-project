package com.aliyun.gts.gmall.manager.front.promotion.dto.input.command;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("分享活动请求")
@Data
public class ShareFissionCommand extends LoginRestCommand {
    @ApiModelProperty(required = true, value = "活动id")
    private Long activityId;
    @ApiModelProperty(value = "分享实例唯一标识")
    private String shareCode;
}
