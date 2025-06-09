package com.aliyun.gts.gmall.manager.front.common.dto;

import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 分页请求
 *
 * @author tiansong
 */
@ApiModel(description = "分页请求")
@Data
public class PageLoginRestQuery extends LoginRestQuery {

    private PageParam page;
    private String cityCode;

    @Override
    public void checkInput() {
        super.checkInput();
        if (page == null || page.getPageNo() <= 0) {
            this.setPage(new PageParam(BizConst.PAGE_NO, BizConst.PAGE_SIZE));
        }
        if  (page.getPageSize() <=0) {
             page.setPageSize(BizConst.PAGE_SIZE);
         }
    }
}
