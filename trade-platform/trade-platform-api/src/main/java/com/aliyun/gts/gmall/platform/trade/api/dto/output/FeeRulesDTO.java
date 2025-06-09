package com.aliyun.gts.gmall.platform.trade.api.dto.output;

import io.swagger.annotations.ApiModelProperty;
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
public class FeeRulesDTO implements Serializable {

    @ApiModelProperty("code")
    private String feeRulesCode;

    @ApiModelProperty("城内顾客最低金额")
    private Long custAmount;

    @ApiModelProperty("城内商家最低金额")
    private Long merchantAmount;

    @ApiModelProperty("城际顾客最低金额")
    private Long interCustAmount;

    @ApiModelProperty("城际商家最低金额")
    private Long interMerchantAmount;

    @ApiModelProperty("删除标记")
    private Integer deleted;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("create_id")
    private String createId;

    @ApiModelProperty("修改时间")
    private Date gmtModified;

    @ApiModelProperty("update_id")
    private String updateId;

    private String operator;

//    private Long id;
//    private String feeRulesCode;
//    private Long custAmount;
//    private Long merchantAmount;
//    private Long interCustAmount;
//    private Long interMerchantAmount;
//    private Integer deleted;
//    private String remark;
//    private Date gmtCreate;
//    private String createId;
//    private Date gmtModified;
//    private String updateId;
//    private String operator;
    
}
