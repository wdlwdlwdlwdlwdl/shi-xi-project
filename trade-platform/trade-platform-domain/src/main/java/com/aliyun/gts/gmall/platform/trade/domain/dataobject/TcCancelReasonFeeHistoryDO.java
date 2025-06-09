package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tc_cancel_reason_fee_history")
public class TcCancelReasonFeeHistoryDO implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("fee code")
    private String reasonFeeCode;

    @ApiModelProperty("code")
    private String cancelReasonCode;

    @ApiModelProperty("取消原因")
    private String cancelReasonName;

    @ApiModelProperty("删除标记")
    private Integer deleted;

    @ApiModelProperty("买家承担")
    private int custFee;

    @ApiModelProperty("卖家承担")
    private int merchatFee;

    @ApiModelProperty("类型，1 新增，2 修改")
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
