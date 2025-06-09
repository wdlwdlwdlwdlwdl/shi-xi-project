package com.aliyun.gts.gmall.manager.front.login.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 手机登录请求
 *
 * @author tiansong
 */
@ApiModel(description = "cas登录")
@Data
public class CasLoginQuery extends AbstractQueryRestRequest {

    @JsonIgnore
    private Long custId;

    @ApiModelProperty("登录用户ID")
    public Long getCustId() {
        CustDTO user = UserHolder.getUser();
        return user == null ? custId : user.getCustId();
    }

    @ApiModelProperty(value = "casKey", required = true)
    private String casKey;

    @ApiModelProperty("语言")
    private String language;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(casKey, I18NMessageUtils.getMessage("casKey.required"));  //# "手机号码不能为空"
    }
}
