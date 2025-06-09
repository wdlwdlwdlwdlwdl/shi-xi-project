package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import java.util.Set;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: CouponInfoReqQuery.java
 * @Description: sku优惠券请求
 * @author zhao.qi
 * @date 2025年2月24日 10:25:49
 * @version V1.0
 */
@Getter
@Setter
public class CouponInfoReqQuery extends AbstractQueryRestRequest {
    private static final long serialVersionUID = 1L;

    /** itemId **/
    private Long itemId;

    /** skuId */
    private Long skuId;

    /** 类目 */
    private Long categoryId;

    /** 品牌 */
    private Long brandId;

    /** 售卖商家 */
    private Set<Long> sellerIds;


    @ApiModelProperty("登录用户ID")
    public Long getCustId() {
        CustDTO user = UserHolder.getUser();
        return user == null ? null : user.getCustId();
    }

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(skuId, I18NMessageUtils.getMessage("skuId") + " [ID] " + I18NMessageUtils.getMessage("cannot.be.empty")); // # "skuID不能为空"
        ParamUtil.nonNull(itemId, I18NMessageUtils.getMessage("itemId") + " [ID] " + I18NMessageUtils.getMessage("cannot.be.empty")); // # "skuID不能为空"
    }
}
