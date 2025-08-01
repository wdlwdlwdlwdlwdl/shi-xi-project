package com.aliyun.gts.gmall.manager.front.common.dto;

import com.aliyun.gts.gmall.framework.api.anns.HeaderValue;
import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.consts.ChannelEnum;
import com.aliyun.gts.gmall.manager.front.common.util.FrontUtils;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录用户请求
 *
 * @author tiansong
 */
@ApiModel("登录用户请求")
@Data
public class LoginRestQuery extends AbstractQueryRestRequest {

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

    @ApiModelProperty("登录用户渠道, 见：ChannelEnum")
    @HeaderValue("Channel")
    private String channel;

    @Override
    public void checkInput() {
        super.checkInput();
        if (getCustId() == null || getCustId() <= 0L) {
            throw new GmallException(CommonResponseCode.NotLogin);
        }
        // 校正一下channel code， 默认 H5
        this.setChannel(ChannelEnum.get(channel).getCode());
    }
}
