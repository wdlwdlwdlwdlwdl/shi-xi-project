package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import java.util.Set;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: SkuSearchPageCategoryExtendThirdRestQuery.java
 * @Description: 商品关键字搜索入参
 * @author zhao.qi
 * @date 2024年11月26日 20:18:13
 * @version V1.0
 */
@Getter
@Setter
public class SkuSearchPageCategoryExtendThirdRestQuery extends AbstractQueryRestRequest {
    private static final long serialVersionUID = 1L;

    private String cityCode;
    private int from = 0;
    private int size = 20;
    private SearchOrderThirdRest order;
    private Set<Long> category1IdsFilter;
    private Set<Long> category2IdsFilter;
    private Set<Long> category3IdsFilter;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notBlank(cityCode, "cityCode:" + I18NMessageUtils.getMessage("cannot.be.empty")); // # "cityCode不能为空"
    }
}
