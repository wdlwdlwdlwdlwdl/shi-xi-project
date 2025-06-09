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
public class CustDeliveryFeeHistoryDTO implements Serializable {

    private Long id;
    private String custFeeCode;
    private Integer deliveryRoute;
    private Integer deliveryType;
    private Long fee;
    private Integer type;
    private String remark;
    private Date gmtCreate;
    private String createId;
    private Date gmtModified;
    private String updateId;
    private String operator;
}
