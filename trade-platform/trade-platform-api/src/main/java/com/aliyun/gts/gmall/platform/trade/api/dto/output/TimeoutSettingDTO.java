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
public class TimeoutSettingDTO implements Serializable {
    private Long id;
    private String timeoutCode;
    private Integer orderStatus;
    private Integer payType;
    private String statusName;
    private String timeRule;
    private Integer timeType;
    private Integer deleted;
    private String remark;
    private Date gmtCreate;
    private String createId;
    private Date gmtModified;
    private String updateId;
    private String operator;

}
