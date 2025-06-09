package com.aliyun.gts.gmall.manager.front.customer.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ByIdCouponQuery extends PageLoginRestQuery {

    @ApiModelProperty("券活动Id")
    private Long couponId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(couponId, I18NMessageUtils.getMessage("coupon")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "券ID不能为空"
    }
}
