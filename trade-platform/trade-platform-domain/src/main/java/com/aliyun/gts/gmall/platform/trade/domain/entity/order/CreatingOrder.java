package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.platform.item.api.dto.output.item.LoanPeriodDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.OrderCheckPayModeDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Customer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * 下单订单，包含多个主订单
 */
@Data
public class CreatingOrder extends AbstractBusinessEntity {

    @ApiModelProperty("主订单列表")
    private List<MainOrder> mainOrders;

    @ApiModelProperty("优惠信息")
    private OrderPromotion promotions;

    @ApiModelProperty("收货信息")
    private ReceiveAddr receiver;

    @ApiModelProperty("发货信息")
    private SalesAddr sales;

    @ApiModelProperty("总金额信息")
    private OrderPrice orderPrice;

    @ApiModelProperty(value = "是否购物查下单")
    private Boolean isFromCart;

    @ApiModelProperty(value = "购物车业务类型")
    private Integer cartType;

    @ApiModelProperty("下单的营销参数, 从前端透传到营销")
    private String promotionSource;

    @ApiModelProperty("下单的扩展参数,从前端透传")
    private Map<String, Object> params;

    @ApiModelProperty("支付渠道")
    private String payChannel;

    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;

    @ApiModelProperty("可用分期列表")
    private List<Integer> installment;

    @ApiModelProperty("可用借贷列表")
    private List<Integer> loan;

    @ApiModelProperty("是否免运费")
    private Boolean freightFeeFree = false;

    @ApiModelProperty("购物车ID")
    private Long cartId;

    @ApiModelProperty("总订单合并分期信息")
    private List<LoanPeriodDTO> sumPriceList;

    @ApiModelProperty("总订单合并贷款信息")
    private List<LoanPeriodDTO> sumLoanPriceList;

    @ApiModelProperty("总订单合并分期信息  install")
    private List<OrderCheckPayModeDTO> installPriceList;

    @ApiModelProperty("总订单合并分期信息  loan")
    private List<OrderCheckPayModeDTO> loanPriceList;

    @ApiModelProperty("临时订单")
    private List<Long> originMainOrderList;

    @ApiModelProperty("是否可以下单")
    private Boolean confirmSuccess = Boolean.TRUE;

    @ApiModelProperty("登录用户的年龄")
    private Integer age = 0;

    @ApiModelProperty("买家id")
    private Long custId;

    @ApiModelProperty("没有城市价格的商品")
    private List<ItemSkuId> noCityPriceSkuIds;

    @ApiModelProperty("PVZ物流方式引导")
    private Boolean pvzPick;

    @ApiModelProperty("postamat物流方式引导")
    private Boolean postamatPick;

    @ApiModelProperty("超出价格类型0，未超出，1分期超出 2 贷款超出")
    private Integer overPriceLimit = 0;

    @ApiModelProperty("错误提示语")
    private String errMsg;



    public String getOrderChannel() {
        return CollectionUtils.isEmpty(mainOrders) ? null : mainOrders.get(0).getOrderChannel();
    }

    public Customer getCustomer() {
        return CollectionUtils.isEmpty(mainOrders) ? null : mainOrders.get(0).getCustomer();
    }

    @ApiModelProperty("非阻断式的错误信息（仅确认订单时）")
    private List<NonblockFail> nonblockFails;

    public void addNonblockFail(NonblockFail fail) {
        if (nonblockFails == null) {
            nonblockFails = new ArrayList<>();
        }
        nonblockFails.add(fail);
    }

    public void addParams(Map<String, Object> params) {
        if (Objects.isNull(params)) {
            return;
        }
        if (Objects.isNull(this.params)) {
            this.params = new HashMap<>();
        }
        this.params.putAll(params);
    }

    public void addParam(String key, Object value) {
        if (Objects.isNull(params)) {
            this.params = new HashMap<>();
        }
        this.params.put(key, value);
    }

    public Object getParam(String key) {
        if (Objects.isNull(params)) {
            return null;
        }
        return params.get(key);
    }
}
