package com.aliyun.gts.gmall.manager.front.customer.dto.input.param;

import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 会员更新信息
 *
 * @author tiansong
 */
@Data
public class CustomerUpdateDO extends AbstractInputParam {
    @ApiModelProperty("login userId")
    private Long id;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("头像")
    private String headUrl;

    @ApiModelProperty("网站账号密码")
    private String pwd;

    @ApiModelProperty("固话区号")
    private String telArea;

    @ApiModelProperty("固话号码")
    private String tel;

    @ApiModelProperty("用户签名")
    private String custSign;

    @ApiModelProperty("备注")
    private String remark;
}
