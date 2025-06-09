package com.aliyun.gts.gmall.manager.front.media.dto;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractCommandRestRequest;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VideoInitializeQuery extends AbstractCommandRestRequest {
    @ApiModelProperty(value = "视频ID")
    private Long videoId;

    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty("登录用户ID")
    public Long getCustId() {
        CustDTO user = UserHolder.getUser();
        return user == null ? null : user.getCustId();
    }

    public void setCustId(Long v) { }

    @Override
    public void checkInput() {
        super.checkInput();
        if (getCustId() == null || getCustId() <= 0L) {
            throw new GmallException(CommonResponseCode.NotLogin);
        }
    }
}
