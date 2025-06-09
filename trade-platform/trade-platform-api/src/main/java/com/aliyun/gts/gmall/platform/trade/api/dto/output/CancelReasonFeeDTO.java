package com.aliyun.gts.gmall.platform.trade.api.dto.output;

import com.aliyun.gts.gmall.framework.i18n.LangText;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/25 10:55
 */
@Data
public class CancelReasonFeeDTO implements Serializable {

    private Long id;
    private String cancelReasonCode;
    private String cancelReasonName;
    /** 取消原因 */
    private Set<LangText> cancelReasonNameI18n;
    private String reasonFeeCode;
    private int custFee;
    private int merchatFee;
    private Integer deleted;
    private String remark;
    private Date gmtCreate;
    private String createId;
    private Date gmtModified;
    private String updateId;
    private String operator;
}
