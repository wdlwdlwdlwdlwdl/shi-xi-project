package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 售后单详情请求
 *
 * @author tiansong
 */
@Data
@ApiModel("售后单详情请求")
public class ReversalDetailRestQuery extends LoginRestQuery {
    @ApiModelProperty("主售后单id")
    private Long primaryReversalId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(primaryReversalId, I18NMessageUtils.getMessage("after.sales.order")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "售后单ID不能为空"
    }
}