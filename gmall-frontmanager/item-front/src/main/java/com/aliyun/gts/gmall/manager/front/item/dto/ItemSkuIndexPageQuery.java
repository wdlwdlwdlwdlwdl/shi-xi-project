package com.aliyun.gts.gmall.manager.front.item.dto;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: ItemSkuIndexPageQuery.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2024年11月12日 09:11:13
 * @version V1.0
 */
@Getter
@Setter
public class ItemSkuIndexPageQuery extends AbstractQueryRestRequest {
    private static final long serialVersionUID = 1L;

    private Integer pageIndex;

    private Integer pageSize;

    private String itemTitle;
    private Long categoryId;
    private String cityCode;

    @Override
    public void checkInput() {
        super.checkInput();
//        ParamUtil.nonNull(pageIndex, "pageIndex can not be null");
//        ParamUtil.expectTrue(pageIndex > 0, "pageIndex not correct");
        ParamUtil.nonNull(pageIndex, "[pageIndex] " + I18NMessageUtils.getMessage("cannot.be.empty"));
        ParamUtil.expectTrue(pageIndex > 0, "[pageIndex] " + I18NMessageUtils.getMessage("not.correct"));
    }
}
