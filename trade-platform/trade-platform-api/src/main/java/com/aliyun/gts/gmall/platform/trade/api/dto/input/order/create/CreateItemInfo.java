package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create;

import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CreateItemInfo extends AbstractInputParam {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;

    @ApiModelProperty(value = "商品SkuId", required = true)
    private Long skuId;

    @ApiModelProperty(value = "商品数量", required = true)
    private Integer itemQty;

    @ApiModelProperty("确认订单时选择的商家")
    private Long sellerId;

    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("物流方式, DeliveryTypeEnum")
    private Integer deliveryType;

    @ApiModelProperty(value = "收件人信息")
    private ReceiverDTO receiver;

    //  以下 是废弃字段 不用！！

    @ApiModelProperty("物流类型")
    private String logisticsType;

    @ApiModelProperty(value = "代客下单商品价格", required = false)
    private Long helpOrderPrice;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(itemId, I18NMessageUtils.getMessage("product")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "商品ID不能为空"
        ParamUtil.nonNull(skuId, I18NMessageUtils.getMessage("product")+" [skuId] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "商品skuId不能为空"
        ParamUtil.nonNull(itemQty, I18NMessageUtils.getMessage("order.qty.empty"));  //# "商品下单数量不能为空"
        ParamUtil.nonNull(sellerId, I18NMessageUtils.getMessage("product.")+" [sellerId] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "确定订单是选择的商家"
        ParamUtil.expectInRange(itemQty, 1, Integer.MAX_VALUE, I18NMessageUtils.getMessage("order.qty.less")+" 1");  //# "商品下单数量不能小于
    }
}
