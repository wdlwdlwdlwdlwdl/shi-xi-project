package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import lombok.Data;

import java.util.List;

@Data
public class CategoryRestQuery extends AbstractQueryRestRequest {

    private List<Long> categoryIds;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(categoryIds, I18NMessageUtils.getMessage("categoryIds")+" "+I18NMessageUtils.getMessage("not.empty"));  //# "categoryIds不能为空"

    }
}
