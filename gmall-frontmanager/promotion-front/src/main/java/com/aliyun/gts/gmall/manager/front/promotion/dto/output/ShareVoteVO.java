package com.aliyun.gts.gmall.manager.front.promotion.dto.output;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("分享裂变助力人信息")
public class ShareVoteVO {
    @ApiModelProperty("助力人昵称")
    private String nickname;
    @ApiModelProperty("助力人头像")
    private String headUrl;
    @ApiModelProperty("助力人id")
    private Long custId;
    @ApiModelProperty("助力人标识符")
    private String custPrimary;
}
