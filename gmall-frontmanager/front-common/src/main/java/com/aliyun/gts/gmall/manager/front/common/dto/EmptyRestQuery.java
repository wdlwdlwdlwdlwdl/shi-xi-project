package com.aliyun.gts.gmall.manager.front.common.dto;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 无参数请求
 * @author tiansong
 */
@ApiModel("无参数请求")
public class EmptyRestQuery extends AbstractQueryRestRequest {
    @Override
    public void checkInput() {
        super.checkInput();
    }

    @JsonIgnore
    private Long custId;

    @ApiModelProperty("登录用户ID")
    public Long getCustId() {
        CustDTO user = UserHolder.getUser();
        return user == null ? custId : user.getCustId();
    }

    public void setCustId(Long v) {
        custId = v;
    }
}
