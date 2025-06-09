package com.aliyun.gts.gmall.manager.front.item.dto;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品详情主接口请求
 *
 * @author tiansong
 */
@ApiModel(description = "商品详情主接口请求")
@Data
public class ItemPageQuery extends AbstractQueryRestRequest {
    @ApiModelProperty(value = "分页索引", required = true)
    private Integer pageIndex;

    @ApiModelProperty(value = "分页大小", required = true)
    private Integer pageSize;

    @ApiModelProperty("登录用户ID")
    public Long getCustId() {
        CustDTO user = UserHolder.getUser();
        return user == null ? null : user.getCustId();
    }
    public void setCustId(Long v) { }

    @Override
    public void checkInput() {
        super.checkInput();
//        ParamUtil.nonNull(pageIndex, "pageIndex can not be null");
        ParamUtil.nonNull(pageIndex, "[pageIndex] " + I18NMessageUtils.getMessage("cannot.be.empty"));
//        ParamUtil.expectTrue(pageIndex > 0, "pageIndex not correct");
        ParamUtil.expectTrue(pageIndex > 0, "[pageIndex] " + I18NMessageUtils.getMessage("not.correct"));
    }
}
