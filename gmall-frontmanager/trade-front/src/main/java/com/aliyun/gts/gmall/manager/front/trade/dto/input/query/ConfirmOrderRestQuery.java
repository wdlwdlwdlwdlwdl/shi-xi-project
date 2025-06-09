package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.AddressVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.PromotionOptionVO;
import com.aliyun.gts.gmall.platform.pay.api.enums.PayTypeEnum;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmItemInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.OrderGroupInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单确认请求
 *
 * @author tiansong
 */
@ApiModel("订单确认请求")
@Data
public class ConfirmOrderRestQuery extends LoginRestQuery {
    @ApiModelProperty(value = "下单渠道", required = true)
    private String  orderChannel;
    @ApiModelProperty(value = "订购商品信息", required = true)
    private List<ConfirmItemInfo> orderItems;
    @ApiModelProperty("支付方式")
    private Integer payType;
    @ApiModelProperty("是否通过购物车下单")
    private Boolean isFromCart;
    @ApiModelProperty("优惠选择")
    private List<PromotionOptionVO> promotionSelection;
    @ApiModelProperty("用户使用的积分个数")
    private String usePointCountInput;
    @ApiModelProperty("积分的原子数量")
    private Long  usePointCount;
    @ApiModelProperty("下单的营销参数, 从前端透传到营销")
    private String promotionSource;
    @ApiModelProperty("确认订单扩展参数")
    private Map<String, Object> params;
    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;
    @ApiModelProperty(value = "支付渠道")
    private String payChannel;
    @ApiModelProperty("分组信息")
    private List<OrderGroupInfo> orderGroupInfoList;
    @ApiModelProperty("购物车ID")
    private Long cartId;
    @ApiModelProperty("贷款周期")
    private Integer loanCycle;
    @ApiModelProperty("订单确认token")
    private String confirmOrderToken;
    @ApiModelProperty("默认收货地地址 支持送货上门且没有指定物流方式的时候 默认设置")
    private Long receiverId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notEmpty(orderItems, I18NMessageUtils.getMessage("order.product.info.required"));  //# "订购商品信息不能为空"
        //# "支付方式不能为空"
        ParamUtil.nonNull(payMode, I18NMessageUtils.getMessage("product") + " [payMode] " + I18NMessageUtils.getMessage("cannot.empty"));
        // 遍历每个商品
        orderItems.forEach(orderItem -> orderItem.checkInput());
        // reset
        this.setOrderChannel(this.getChannel());
        this.setPayType(PayTypeEnum.ONLINE_PAY.getCode());
        // 优惠列表只传递选中的数据
        if (CollectionUtils.isNotEmpty(promotionSelection)) {
            promotionSelection = promotionSelection
                .stream()
                .filter(v -> StringUtils.isNotBlank(v.getOptionId()))
                .collect(Collectors.toList());
            promotionSelection.forEach(v -> v.setSelected(Boolean.TRUE));
        }
        // 使用的积分数量进行转换
        this.setUsePointCount(ItemUtils.pointInput(usePointCountInput));
    }
}
