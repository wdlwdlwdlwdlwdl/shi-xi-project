package com.aliyun.gts.gmall.manager.front.sourcing.vo.bc;

import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.ScoringGroup;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel("开标配置")
@Data
public class BidChosingConfigVO extends BaseVO {

    @ApiModelProperty("寻源id")
    Long sourcingId;
    @ApiModelProperty("评标开始")
    Date start;
    @ApiModelProperty("评标截止")
    Date end;
    @ApiModelProperty("参与用户组")
    List<ScoringGroup> userGroup;
    @ApiModelProperty("评标维度")
    List<SectionConfigVO> sections;
    @ApiModelProperty("0指定用户 1随机用户")
    int randomUser;
    @ApiModelProperty("0草稿 1生效")
    Integer status;

    String sourcingName;

}
