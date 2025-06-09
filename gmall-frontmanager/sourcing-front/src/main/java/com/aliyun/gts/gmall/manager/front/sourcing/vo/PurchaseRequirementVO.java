package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.PurchaseRequirement;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PurchaseRequirementVO extends PurchaseRequirement {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deliverStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deliverEndTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date priceStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date priceEndTime;
}
