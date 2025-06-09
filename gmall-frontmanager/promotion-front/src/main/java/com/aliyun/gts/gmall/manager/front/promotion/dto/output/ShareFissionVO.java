package com.aliyun.gts.gmall.manager.front.promotion.dto.output;

import com.alibaba.fastjson.JSONArray;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("分享裂变VO")
public class ShareFissionVO extends AbstractOutputInfo {
    @ApiModelProperty("活动id")
    private Long activityId;
    @ApiModelProperty("助力列表")
    private List<ShareVoteVO> voteList;
    @ApiModelProperty("唯一分享code")
    private String shareCode;

    @ApiModelProperty("状态,表示1表示助力中,大于等于10表示助力完成")
    private Integer status;

    @ApiModelProperty("需要助力的人数")
    private Integer shareCnt;
    @ApiModelProperty("已经助力人数")
    private Integer voteCnt;

    @ApiModelProperty("用户nick")
    private String nickname;

    @ApiModelProperty("中奖奖品名称信息")
    private JSONArray prizeInfo;

    @ApiModelProperty("他人分享，我是否已经助力")
    private Boolean otherShareVoteFlag;

}
