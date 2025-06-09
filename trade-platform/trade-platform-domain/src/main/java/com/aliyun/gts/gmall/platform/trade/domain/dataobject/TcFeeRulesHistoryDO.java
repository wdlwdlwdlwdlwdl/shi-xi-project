package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tc_fee_rules_history")
public class TcFeeRulesHistoryDO implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
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

    @ApiModelProperty("操作类型")
    private Integer type;

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
}
