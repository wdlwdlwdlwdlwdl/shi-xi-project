package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("sku评分平均分请求")
public class SkuAverageScoreQuery extends AbstractQueryRestRequest {
    /**
     * skuId
     */
    private Long skuId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(skuId, I18NMessageUtils.getMessage("skuId")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "skuID不能为空"
    }
}
