package com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "售后单查询结果")
public class MainReversalDTO extends AbstractOutputInfo {

    @ApiModelProperty("售后主单ID")
    private Long primaryReversalId;

    @ApiModelProperty("主订单信息")
    private Long primaryOrderId;

    @ApiModelProperty("售后子单列表")
    private List<SubReversalDTO> subReversals;

    @ApiModelProperty("客户ID")
    private Long custId;

    @ApiModelProperty("客户名称")
    private String custName;

    @ApiModelProperty("firstName")
    private String firstName;

    @ApiModelProperty("lastName")
    private String lastName;

    @ApiModelProperty("退款总金额")
    private Long cancelAmt;

    @ApiModelProperty("退换总件数")
    private Integer cancelQty;

    @ApiModelProperty("售后类型, ReversalTypeEnum")
    private Integer reversalType;

    @ApiModelProperty("申请售后渠道, 同OrderChannelEnum")
    private String reversalChannel;

    @ApiModelProperty("ReversalStatusEnum")
    private Integer reversalStatus;

    @ApiModelProperty("售后原因code")
    private Integer reversalReasonCode;

    @ApiModelProperty("售后原因内容")
    private String reversalReasonContent;

    @ApiModelProperty("是否收到货物")
    private Boolean itemReceived;

    @ApiModelProperty("买家备注")
    private String custMemo;

    @ApiModelProperty("买家举证图片")
    private List<String> custMedias;

    @ApiModelProperty("卖家备注")
    private String sellerMemo;

    @ApiModelProperty("卖家举证图片")
    private List<String> sellerMedias;

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("卖家名称")
    private String sellerName;

    @ApiModelProperty("订单信息")
    private MainOrderDTO orderInfo;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("订单创建时间")
    private Date orderCreate;

    @ApiModelProperty("退单完成时间")
    private Date reversalCompletedTime;

    @ApiModelProperty("扩展字段")
    private ReversalFeatureDTO reversalFeatures;

    @ApiModelProperty("操作流水, 详情接口时返回")
    private List<ReversalFlowDTO> flows;

    @ApiModelProperty("版本号")
    private Long version;

    @ApiModelProperty("bin_or_iin")
    private String binOrIin;
}
