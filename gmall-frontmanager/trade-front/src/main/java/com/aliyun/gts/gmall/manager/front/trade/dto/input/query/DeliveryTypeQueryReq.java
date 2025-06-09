package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: DeliveryTypeQueryReq.java
 * @Description: 物流方式查询
 * @author zhao.qi
 * @date 2024年11月26日 11:26:24
 * @version V1.0
 */
@Getter
@Setter
public class DeliveryTypeQueryReq extends AbstractCommandRpcRequest {
    private static final long serialVersionUID = 1L;

    /** skuId */
    private Long skuId;
    /** sellerId */
    private Long sellerId;
    /** 城市编码 */
    private String cityCode;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(skuId, "[skuId]: " + I18NMessageUtils.getMessage("param.required")); // # "skuId必须不为空"
        ParamUtil.nonNull(sellerId, "[sellerId]: " + I18NMessageUtils.getMessage("param.required")); // # "sellerId必须不为空"
        ParamUtil.notBlank(cityCode, "[cityCode]: " + I18NMessageUtils.getMessage("param.required")); // # "cityCode不能为空"
    }
}
