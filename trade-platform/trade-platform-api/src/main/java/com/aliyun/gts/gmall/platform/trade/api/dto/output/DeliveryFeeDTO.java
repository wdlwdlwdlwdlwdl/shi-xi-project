package com.aliyun.gts.gmall.platform.trade.api.dto.output;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
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
public class DeliveryFeeDTO implements Serializable {

    private Long id;
    private String feeCode;
    private Integer deliveryRoute;
    private Integer deleted;
    private String categoryId;
    private String categoryName;
    private String merchantCode;
    private String merchantName;
    private Integer deliveryType;
    private Long fee;
    private String path;
    private String remark;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;
    private String createId;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;
    private String updateId;
    private String operator;
    private Integer active;

    @ApiModelProperty("是否类目全部")
    private String isCategoryAll;

    @ApiModelProperty("是否卖家全部")
    private String isMerchantAll;
}
