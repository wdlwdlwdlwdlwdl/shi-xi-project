package com.aliyun.gts.gmall.platform.trade.domain.entity.price;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.gts.gmall.framework.domain.extend.ExtendComponent;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.*;

@Data
public class ConfirmPrice extends ExtendComponent {

    @ApiModelProperty("确认收货实付金额, 减去了售中退款")
    private Long confirmRealAmt;

    @ApiModelProperty("确认收货积分金额, 减去了售中退款")
    private Long confirmPointAmt;

    @ApiModelProperty("确认收货打款总金额, 减去了售中退款")
    @JSONField(serialize = false)
    public Long getConfirmTotalAmt() {
        return NumUtils.getNullZero(confirmRealAmt) + NumUtils.getNullZero(confirmPointAmt);
    }

    @ApiModelProperty("分账金额, 合计等于 confirmPointAmt, 分账规则: SeparateRule")
    private Map<String, Long> separatePointAmt;

    @ApiModelProperty("分账金额, 合计等于 confirmRealAmt, 分账规则: SeparateRule")
    private Map<String, Long> separateRealAmt;

    @ApiModelProperty("分账总金额, separatePointAmt + separateRealAmt")
    @JSONField(serialize = false)
    public Map<String, Long> getSeparateAmt() {
        return CommUtils.addMergeLong(separatePointAmt, separateRealAmt);
    }
}
