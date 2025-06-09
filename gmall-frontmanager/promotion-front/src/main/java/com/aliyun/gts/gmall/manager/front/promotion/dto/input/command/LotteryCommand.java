package com.aliyun.gts.gmall.manager.front.promotion.dto.input.command;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("分享活动请求")
@Data
public class LotteryCommand extends LoginRestCommand {
    @ApiModelProperty(required = true, value = "活动id")
    private Long activityId;

    private Boolean jfLottery;

    private String outId;
}
