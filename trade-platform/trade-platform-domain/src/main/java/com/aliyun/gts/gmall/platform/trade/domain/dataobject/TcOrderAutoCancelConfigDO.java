package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "tc_order_auto_cancel_config")
public class TcOrderAutoCancelConfigDO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("取消原因")
    private String cancelReasonCode;

    @ApiModelProperty("状态")
    private Integer orderStatus;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("修改时间")
    private Date gmtModified;

    @ApiModelProperty("删除标记")
    private Integer deleted;

}
