package com.aliyun.gts.gmall.center.trade.domain.dataobject;

import com.aliyun.gts.gmall.platform.trade.domain.typehandler.MybatisJsonTypeHandler;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

@Data
@TableName("tc_order_invoice")
public class TcOrderInvoiceDO implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("主订单id")
    private Long primaryOrderId;

    @ApiModelProperty("销售类型")
    private Integer saleType;

    @ApiModelProperty("主单金额")
    private String masterOrderAmount;

    @ApiModelProperty("发票类型：0普通发票，1专票")
    private Integer invoiceType;

    @ApiModelProperty("远程返回的 开票id")
    private Long invoiceId;

    @ApiModelProperty("请求号")
    private String requestNo;

    @ApiModelProperty("发票代码")
    private String invoiceCode;

    @ApiModelProperty("发票号码")
    private String invoiceNo;

    @ApiModelProperty("开票金额")
    private String invoiceAmount;

    @ApiModelProperty("开票时间")
    private Date invoiceTime;

    @ApiModelProperty("存储pdf文件key")
    private String pdfKey;

    @ApiModelProperty("存储png预览key")
    private String pngKey;

    @ApiModelProperty("开票状态:未开票,已开票,部分开票,已撤销,已作废")
    private Integer status;

    @ApiModelProperty("撤销发票 默认0未发起，1撤销中， 3同意 4拒绝")
    private Integer invoiceReject;

    @ApiModelProperty("撤销发票拒绝原因")
    private String invoiceRejectReason;

    @ApiModelProperty("开票抬头")
    private String invoiceTitle;

    @ApiModelProperty("申请开票详情")
    private String invoiceApplyReq;

    @ApiModelProperty("开票结果详情")
    private String invoiceResult;

    @ApiModelProperty("卖家")
    private Long sellerId;

    @ApiModelProperty("买家")
    private Long custId;

    @ApiModelProperty("扩展字段")
    @TableField(typeHandler = MybatisJsonTypeHandler.class)
    private HashMap features;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @ApiModelProperty("删除")
    private Integer deleted;

}
