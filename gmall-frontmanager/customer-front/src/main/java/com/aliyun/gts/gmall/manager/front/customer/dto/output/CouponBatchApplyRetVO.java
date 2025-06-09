package com.aliyun.gts.gmall.manager.front.customer.dto.output;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class CouponBatchApplyRetVO implements Serializable {

    private String returnCode;

    private String message;

    public CouponBatchApplyRetVO(String returnCode, String message) {
        this.returnCode = returnCode;
        this.message = message;
    }
}
