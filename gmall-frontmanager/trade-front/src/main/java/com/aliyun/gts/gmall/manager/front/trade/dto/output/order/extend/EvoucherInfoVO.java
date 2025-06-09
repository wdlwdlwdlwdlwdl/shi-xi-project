package com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.Date;

import com.aliyun.gts.gmall.framework.server.util.DateTimeUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.EvoucherStatusDisplay;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("电子凭证详情")
public class EvoucherInfoVO {
    @ApiModelProperty("生效时间, null 代表购买后立即生效")
    private Date    startTime;
    @ApiModelProperty("失效时间, null 代表永不失效")
    private Date    endTime;
    @ApiModelProperty("电子凭证码")
    private Long    evCode;
    @ApiModelProperty("状态 see EvoucherStatusEnum")
    private Integer status;
    @ApiModelProperty("状态 see EvoucherStatusEnum")
    private String  statusName;
    @ApiModelProperty("核销时间")
    private Date    writeOffTime;

    @ApiModelProperty("有效期字符串")
    public String getEvoucherDisplay() {
        if (startTime == null) {
            return endTime == null ? I18NMessageUtils.getMessage("valid.until") : "截止到 " + DateTimeUtils.nyrFormat(endTime);  //# "长期有效"
        }
        return endTime == null ? DateTimeUtils.nyrFormat(startTime) + " - 长期有效" :
                DateTimeUtils.nyrFormat(startTime) + " - " + DateTimeUtils.nyrFormat(endTime);
    }

    /**
     * 重新计算status
     */
    public void calcStatus() {
        EvoucherStatusDisplay evoucherStatusDisplay = EvoucherStatusDisplay.codeOf(status);
        if (EvoucherStatusDisplay.NOT_USED.getCode().equals(status)) {
            Date now = new Date();
            if (startTime != null && startTime.after(now)) {
                evoucherStatusDisplay = EvoucherStatusDisplay.NOT_EFFECTIVE;
            }
            if (endTime != null && endTime.before(now)) {
                evoucherStatusDisplay = EvoucherStatusDisplay.EXPIRED;
            }
        }
        this.status = evoucherStatusDisplay.getCode();
        this.statusName = evoucherStatusDisplay.getName();
    }
}
