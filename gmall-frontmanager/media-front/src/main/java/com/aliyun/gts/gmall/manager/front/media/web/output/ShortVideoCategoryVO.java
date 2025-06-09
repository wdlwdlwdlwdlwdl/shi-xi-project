package com.aliyun.gts.gmall.manager.front.media.web.output;

import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoCategoryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ShortVideoCategoryVO extends ShortVideoCategoryDTO {
    private static final long serialVersionUID=1L;

    @ApiModelProperty("分类名称")
    private String categoryStatusName;

    @ApiModelProperty("数据状态名称")
    private String statusName;

    @ApiModelProperty(value = "关联短视频数量")
    private Long relatedVideoNum;

    @ApiModelProperty(value = "关联短视频数量")
    private Long relatedNormalVideoNum;
}
