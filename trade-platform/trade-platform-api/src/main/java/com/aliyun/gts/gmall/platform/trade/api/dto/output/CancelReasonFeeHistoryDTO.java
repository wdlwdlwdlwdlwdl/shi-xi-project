package com.aliyun.gts.gmall.platform.trade.api.dto.output;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/25 10:55
 */
@Data
public class CancelReasonFeeHistoryDTO implements Serializable {

    private Long id;
    private String reasonFeeCode;
    private String cancelReasonCode;
    private String cancelReasonName;
    private int custFee;
    private int merchatFee;
    private Integer deleted;
    private Integer type;
    private String remark;
    private Date gmtCreate;
    private String createId;
    private Date gmtModified;
    private String updateId;
    private String operator;
}
