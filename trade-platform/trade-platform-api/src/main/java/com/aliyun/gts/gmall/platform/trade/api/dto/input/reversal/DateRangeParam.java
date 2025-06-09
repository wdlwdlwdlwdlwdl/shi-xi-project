package com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal;

import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class DateRangeParam extends AbstractInputParam {

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;
}
