package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.extend;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class OrderExtendDTO extends AbstractOutputInfo {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("主订单ID")
    private Long primaryOrderId;

    @ApiModelProperty("子订单ID")
    private Long orderId;

    @ApiModelProperty("买家ID")
    private Long custId;

    @ApiModelProperty("扩展属性类型")
    private String extendType;

    @ApiModelProperty("扩展属性key")
    private String extendKey;

    @ApiModelProperty("扩展属性value")
    private String extendValue;

    @ApiModelProperty("扩展属性名称")
    private String extendName;

    @ApiModelProperty("是否有效, 1有效, 0无效")
    private Integer valid;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
