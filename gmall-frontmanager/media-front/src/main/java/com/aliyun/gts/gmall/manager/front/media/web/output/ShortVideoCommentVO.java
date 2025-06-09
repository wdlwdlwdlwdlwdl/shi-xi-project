package com.aliyun.gts.gmall.manager.front.media.web.output;

import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoCommentDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ShortVideoCommentVO extends ShortVideoCommentDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户信息")
    private CustomerDTO customerInfo;

    @ApiModelProperty(value = "该评论回复列表")
    private List<SubShortVideoCommentVO> subComments;

    @ApiModelProperty(value = "是否标识同一人")
    private Boolean sameUser;
}
