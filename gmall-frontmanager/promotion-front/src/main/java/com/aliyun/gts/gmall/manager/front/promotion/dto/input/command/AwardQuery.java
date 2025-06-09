package com.aliyun.gts.gmall.manager.front.promotion.dto.input.command;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("分享活动请求")
@Data
public class AwardQuery  extends PageLoginRestQuery {
    @ApiModelProperty(required = true, value = "活动id")
    private Long activityId;
    /**
     * 中奖记录ID
     */
    private Long id;
    /**
     * 优惠类型
     */
    private Integer prizeType;
    /**
     *  中奖状态
     *      win(5, "中奖,待发奖"),
     *     send(10, "已发奖"),
     */
    private Integer awardStatus;

    private String prizeName;
}
