package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tc_cust_delivery_fee")
public class TcCustDeliveryFeeDO implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("code")
    private String custFeeCode;

    @ApiModelProperty("code")
    private Integer deliveryRoute;

    private Integer deliveryType;

    @ApiModelProperty("费用")
    private Long fee;

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

    private Integer active;

}
