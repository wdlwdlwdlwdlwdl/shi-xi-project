package com.aliyun.gts.gmall.manager.front.media.web.output;

import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoLabelDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ShortVideoLabelVO extends ShortVideoLabelDTO {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "关联短视频数量")
    private Long relatedVideoNum;
}
