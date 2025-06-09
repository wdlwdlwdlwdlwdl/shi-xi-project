package com.aliyun.gts.gmall.center.trade.api.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class EvoucherDTO extends AbstractOutputInfo {

    @ApiModelProperty("子订单ID")
    private Long orderId;

    @ApiModelProperty("生效时间, null 代表购买后立即生效")
    private Date startTime;

    @ApiModelProperty("失效时间, null 代表永不失效")
    private Date endTime;

    @ApiModelProperty("电子凭证码")
    private Long evCode;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("修改时间")
    private Date gmtModified;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("核销时间")
    private Date writeOffTime;

    @ApiModelProperty("核销操作人ID")
    private Long writeOffOpId;

    @ApiModelProperty("核销操作人名称")
    private String writeOffOpName;

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("卖家（店铺）名称")
    private String sellerName;

    @ApiModelProperty("顾客ID")
    private Long custId;

    @ApiModelProperty("顾客名称")
    private String custName;
}
