package com.aliyun.gts.gmall.manager.front.item.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractPageQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品详情中的评价列表
 *
 * @author tiansong
 */
@Data
@ApiModel("商品详情中的评价列表")
public class EvaluationRestQuery extends AbstractPageQueryRestRequest {
    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(itemId, I18NMessageUtils.getMessage("product")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品ID不能为空"
    }
}
