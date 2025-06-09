package com.aliyun.gts.gmall.platform.trade.domain.entity.reversal;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.ReversalFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 售后主单
 */
@Data
public class MainReversal {

    @ApiModelProperty("售后主单ID")
    private Long primaryReversalId;

    @ApiModelProperty("主订单信息")
    private MainOrder mainOrder;

    @ApiModelProperty("售后子单列表")
    private List<SubReversal> subReversals;

    @ApiModelProperty("客户ID")
    private Long custId;

    @ApiModelProperty("客户名称")
    private String custName;

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

    @ApiModelProperty("扩展字段")
    private ReversalFeatureDO reversalFeatures;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("订单创建时间")
    private Date orderCreate;

    @ApiModelProperty("退单完成时间")
    private Date reversalCompletedTime;

    @ApiModelProperty("操作流水")
    private List<TcReversalFlowDO> flows;

    @ApiModelProperty("买家确认收到退款 ")
    private Boolean buyerConfirmRefund;
    @ApiModelProperty("buyerConfirmRefund 买家确认退款的单号")
    private String bcrNumber;
    @ApiModelProperty("buyerConfirmRefund 买家确认退款的备注")
    private String bcrMemo;
    @ApiModelProperty("firstName")
    private String firstName;

    @ApiModelProperty("lastName")
    private String lastName;


    @ApiModelProperty("bin_or_iin")
    private String binOrIin;

    @ApiModelProperty("版本号")
    private Long version;

    public ReversalFeatureDO reversalFeatures() {
        if (reversalFeatures == null) {
            reversalFeatures = new ReversalFeatureDO();
        }
        return reversalFeatures;
    }

    /**
     * 退子单及运费(服务单)的金额, 汇总后等于 主单金额
     */
    @JSONField(serialize = false)
    public List<SubRefundFee> getAllSubFee() {
        List<SubRefundFee> list = new ArrayList<>();
        for (SubReversal sub : subReversals) {
            list.add(SubRefundFee.subReversal(sub));
        }
        //不退运费
  /*      SubRefundFee freight = SubRefundFee.freight(this);
        list.add(freight);*/
        return list;
    }
}
