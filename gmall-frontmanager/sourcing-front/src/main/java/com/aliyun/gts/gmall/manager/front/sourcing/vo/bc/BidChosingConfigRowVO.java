package com.aliyun.gts.gmall.manager.front.sourcing.vo.bc;

import com.aliyun.gts.gmall.manager.front.sourcing.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel("开标配置")
@Data
public class BidChosingConfigRowVO extends BaseVO {

    @ApiModelProperty("寻源id")
    Long sourcingId;

    List<Date> chosingTime;

    @ApiModelProperty("0草稿 1未开始  2进行中  3已完成")
    Integer status;

    String sourcingName;

}
