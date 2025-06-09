package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("评价图片请求")
public class SkuEvaluatePictureQuery extends AbstractQueryRestRequest {
    /**
     * skuId
     */
    private Long skuId;

    /**
     * 好评（评分大于4），差评（评分小于4）
     */
    private Boolean condition;
    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(skuId, I18NMessageUtils.getMessage("skuId")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "skuID不能为空"
    }
}
