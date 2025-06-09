package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: SearchActionSuggestThirdRestQuery.java
 * @Description: 搜索建议入参
 * @author zhao.qi
 * @date 2024年11月26日 20:18:13
 * @version V1.0
 */
@Getter
@Setter
public class SearchActionSuggestThirdRestQuery extends AbstractQueryRestRequest {
    private static final long serialVersionUID = 1L;

    private String keyword;
    private String cityCode;
    private int from = 0;
    private int size = 20;

    public void checkInput() {
        super.checkInput();
        ParamUtil.notBlank(keyword, "[keyword]: " + I18NMessageUtils.getMessage("cannot.be.empty")); // # "keyword不能为空"
    }
}
