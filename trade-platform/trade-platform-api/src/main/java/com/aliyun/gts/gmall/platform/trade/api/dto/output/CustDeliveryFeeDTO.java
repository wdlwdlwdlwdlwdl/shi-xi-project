package com.aliyun.gts.gmall.platform.trade.api.dto.output;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

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
public class CustDeliveryFeeDTO implements Serializable {

    private Long id;
    private String custFeeCode;
    private Integer deliveryRoute;
    private Integer deliveryType;
    private Long fee;
    private String remark;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;
    private String createId;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;
    private String updateId;
    private String operator;
    private Integer active;
}
