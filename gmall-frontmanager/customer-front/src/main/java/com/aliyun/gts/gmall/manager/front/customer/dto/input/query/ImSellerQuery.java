package com.aliyun.gts.gmall.manager.front.customer.dto.input.query;

import com.aliyun.gts.gmall.framework.api.anns.HeaderValue;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.platform.gim.common.type.LoginTargetType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/8/11 14:53
 */
@Data
public class ImSellerQuery extends AbstractQueryRestRequest {
    @ApiModelProperty(value = "卖家id")
    private Long sellerId;

    @ApiModelProperty(value = "see LoginTargetType")
    private Integer targetType;

    public Long getCustId() {
        CustDTO user = UserHolder.getUser();
        return user == null ? null : user.getCustId();
    }

    public void setCustId(Long v) { }

    @Override
    public void checkInput() {
        super.checkInput();
        //ParamUtil.nonNull(sellerId, "卖家id不能为空");
        ParamUtil.nonNull(getCustId(), "custid"+ I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
        if (targetType == null) {
            // 兼容原有,默认值
            targetType = LoginTargetType.WAP_SINGLE;
        }
    }
}
