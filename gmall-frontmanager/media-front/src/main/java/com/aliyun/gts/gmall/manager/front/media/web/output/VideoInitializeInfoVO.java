package com.aliyun.gts.gmall.manager.front.media.web.output;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class VideoInitializeInfoVO {

    @ApiModelProperty(value = "是否关注店铺")
    private Boolean interst;

    @ApiModelProperty(value = "是否点赞")
    private Boolean likes;

    @ApiModelProperty(value = "取消关注店铺ID")
    private Long interstId;

    @ApiModelProperty(value = "取消点赞ID")
    private Long likesId;

    @ApiModelProperty(value = "点赞数")
    private Integer likesNum;

    @ApiModelProperty(value = "评论数")
    private Integer commentsNum;

    @ApiModelProperty(value = "分享数")
    private Integer shareNum;
}
