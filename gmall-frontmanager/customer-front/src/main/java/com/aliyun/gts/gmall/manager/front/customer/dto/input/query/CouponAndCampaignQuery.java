package com.aliyun.gts.gmall.manager.front.customer.dto.input.query;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 俊贤
 * @date 2021/03/12
 */
@Data
public class CouponAndCampaignQuery extends LoginRestCommand {
    @ApiModelProperty("券活动IDs")
    private List<Long> couponIds;
}