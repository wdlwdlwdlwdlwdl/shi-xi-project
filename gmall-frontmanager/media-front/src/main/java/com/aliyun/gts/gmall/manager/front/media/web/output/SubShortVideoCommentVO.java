package com.aliyun.gts.gmall.manager.front.media.web.output;

import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoCommentDTO;
import com.aliyun.gts.gmall.platform.operator.api.dto.output.OperatorDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SubShortVideoCommentVO extends ShortVideoCommentDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "店铺操作者信息")
    private OperatorDTO sellerOperatorInfo;

    @ApiModelProperty(value = "是否标识同一人")
    private Boolean sameUser;
}
