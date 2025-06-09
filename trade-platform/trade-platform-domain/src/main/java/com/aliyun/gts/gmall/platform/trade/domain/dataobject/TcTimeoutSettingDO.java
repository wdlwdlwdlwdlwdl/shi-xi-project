package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tc_timeout_setting")
public class TcTimeoutSettingDO implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("code")
    private String timeoutCode;

    @ApiModelProperty("status")
    private Integer orderStatus;

    @ApiModelProperty("pay_type")
    private Integer payType;

    @ApiModelProperty("statusName")
    private String statusName;

    @ApiModelProperty("timeRule")
    private String timeRule;

    private Integer timeType;

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
}
