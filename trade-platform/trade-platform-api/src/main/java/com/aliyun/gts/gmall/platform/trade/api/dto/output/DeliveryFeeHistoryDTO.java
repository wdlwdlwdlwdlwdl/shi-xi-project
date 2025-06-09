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
public class DeliveryFeeHistoryDTO implements Serializable {

    private Long id;
    private String feeCode;
    private Integer deliveryRoute;
    private Integer deleted;
    private String categoryId;
    private String categoryName;
    private String merchantCode;
    private String merchantName;
    private Integer deliveryType;
    private Integer type;
    private String path;
    private String remark;
    private Date gmtCreate;
    private String createId;
    private Date gmtModified;
    private String updateId;
    private String operator;
}
