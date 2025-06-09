package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;

import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "查询购物车")
public class QueryCartRestCommand extends LoginRestQuery {

    private String cityCode;

    private PageParam page;

    @Override
    public void checkInput() {
        super.checkInput();
        if (page == null || page.getPageNo() <= 0) {
            this.setPage(new PageParam(BizConst.PAGE_NO, BizConst.PAGE_SIZE));
        }
        if  (page.getPageSize() <=0) {
            page.setPageSize(BizConst.PAGE_SIZE);
        }
        ParamUtil.nonNull(this.cityCode, I18NMessageUtils.getMessage("city.code.required"));  //# "城市校验"
    }
}
