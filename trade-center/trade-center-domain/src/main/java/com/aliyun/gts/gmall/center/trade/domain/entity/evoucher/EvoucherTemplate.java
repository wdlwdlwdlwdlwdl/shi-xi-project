package com.aliyun.gts.gmall.center.trade.domain.entity.evoucher;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class EvoucherTemplate {

    @ApiModelProperty("购买后day天有效")
    public static final int TYPE_1 = 1;

    @ApiModelProperty("购买后到end有效")
    public static final int TYPE_2 = 2;

    @ApiModelProperty("start到end有效")
    public static final int TYPE_3 = 3;

    @ApiModelProperty("长期有效")
    public static final int TYPE_4 = 4;


    @ApiModelProperty("电子凭证类型")
    private Integer type;
    @ApiModelProperty("type_3类型的开始日期")
    private Date start;
    @ApiModelProperty("type_2、type_3类型的结束日期")
    private Date end;
    @ApiModelProperty("type_1类型的 天数")
    private Integer day;
}
