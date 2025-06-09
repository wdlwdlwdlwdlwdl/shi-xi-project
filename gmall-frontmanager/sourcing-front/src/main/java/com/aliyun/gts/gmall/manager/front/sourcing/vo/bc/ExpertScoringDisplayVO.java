package com.aliyun.gts.gmall.manager.front.sourcing.vo.bc;

import com.aliyun.gts.gcai.platform.sourcing.common.model.UserDisplayDO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("专家打分显示")
@Data
public class ExpertScoringDisplayVO extends UserDisplayDO {
    Double score;
}
