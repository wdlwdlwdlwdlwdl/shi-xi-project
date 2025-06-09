package com.aliyun.gts.gmall.center.trade.domain.dataobject.evoucher;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

@Data
public class EvoucherFeatureDO {

    @ApiModelProperty("核销时间")
    private Date writeOffTime;

    @ApiModelProperty("核销操作人ID")
    private Long writeOffOpId;

    @ApiModelProperty("核销操作人名称")
    private String writeOffOpName;

    @ApiModelProperty("销毁时间")
    private Date disabledTime;


    // ===================================

    public static EvoucherFeatureDO parse(String features) {
        if (StringUtils.isBlank(features)) {
            return new EvoucherFeatureDO();
        }
        return JSON.parseObject(features, EvoucherFeatureDO.class);
    }

    public String toFeatures() {
        return JSON.toJSONString(this);
    }
}
