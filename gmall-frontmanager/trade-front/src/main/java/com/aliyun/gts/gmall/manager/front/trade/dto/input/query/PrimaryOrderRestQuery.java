package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.OnlinePayChannelEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 主订单操作
 *
 * @author tiansong
 */
@ApiModel("主订单操作")
@Data
public class PrimaryOrderRestQuery extends LoginRestQuery {
    @ApiModelProperty(value = "主订单id", required = true)
    private Long primaryOrderId;

    @ApiModelProperty(value = "主订单id-列表（传入多个primaryOrderId）", required = true)
    private List<Long> primaryOrderIdList;

    @ApiModelProperty("订单渠道")
    private String orderChannel;
    @ApiModelProperty("支付渠道")
    private String payChannel;

    @Override
    public void checkInput() {
        super.checkInput();
//下面两行校验代码要注释掉，因为有两个接口都用到了这个类，一个是 getDetail(传primaryOrderId) ,还有一个是 getDetailNew(传primaryOrderIdList-列表) ，不能校验primaryOrderId
//        ParamUtil.nonNull(primaryOrderId, I18NMessageUtils.getMessage("main.order")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "主订单ID不能为空"
//        ParamUtil.expectTrue(primaryOrderId > 0L, I18NMessageUtils.getMessage("main.order")+"ID"+I18NMessageUtils.getMessage("must.be.pos.int"));  //# "主订单ID必须为正整数"
        // reset
        this.setOrderChannel(this.getChannel());
        OnlinePayChannelEnum onlinePayChannelEnum = OnlinePayChannelEnum.getByCode(payChannel);
        if (onlinePayChannelEnum == null) {
            this.setPayChannel(OnlinePayChannelEnum.ALIPAY.getCode());
        }
    }
}
