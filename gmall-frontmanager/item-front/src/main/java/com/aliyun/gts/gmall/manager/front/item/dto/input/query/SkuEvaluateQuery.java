package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractPageQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("评价详情列表请求")
public class SkuEvaluateQuery extends AbstractPageQueryRestRequest {
    /**
     * skuId
     */
    private Long skuId;


    /**
     * 好评（评分大于4），差评（评分小于4）
     *  1 全部  2好评  3差评
     */
    private int gradeClassify = 0;
    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(skuId, I18NMessageUtils.getMessage("skuId")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "skuID不能为空"
    }

}
