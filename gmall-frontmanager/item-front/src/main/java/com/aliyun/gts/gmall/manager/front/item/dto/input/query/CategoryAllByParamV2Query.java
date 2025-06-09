package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CategoryAllByParamV2Query extends AbstractQueryRestRequest {
    /**
     * 父类目
     */
    private Long parentId;
    /**
     * 当前类目
     */
    private Long id;
    /**
     * 城市
     */
    private String city;
    @ApiModelProperty("登录用户ID")
    public Long getCustId() {
        CustDTO user = UserHolder.getUser();
        return user == null ? null : user.getCustId();
    }
    public void setCustId(Long v) { }

    @Override
    public void checkInput() {
        super.checkInput();

    }
}
