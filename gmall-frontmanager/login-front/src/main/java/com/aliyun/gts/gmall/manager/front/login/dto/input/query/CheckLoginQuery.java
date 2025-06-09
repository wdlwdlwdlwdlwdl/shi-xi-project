package com.aliyun.gts.gmall.manager.front.login.dto.input.query;

import com.aliyun.gts.gmall.framework.api.anns.HeaderValue;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 校验是否登录请求
 *
 * @author tiansong
 */
@ApiModel(description = "校验是否登录请求")
@Data
public class CheckLoginQuery extends AbstractQueryRestRequest {
    @ApiModelProperty("登录用户ID")
    public Long getCustId() {
        CustDTO user = UserHolder.getUser();
        return user == null ? null : user.getCustId();
    }

    public void setCustId(Long v) { }

    @ApiModelProperty("登录用户渠道, 见：ChannelEnum")
    @HeaderValue("Channel")
    private String channel;

    @Override
    public void checkInput() {
        super.checkInput();
    }
}
