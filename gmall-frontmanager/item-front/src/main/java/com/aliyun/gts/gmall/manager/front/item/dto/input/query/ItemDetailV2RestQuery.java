package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractPageQueryRestRequest;
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
public class ItemDetailV2RestQuery extends AbstractQueryRestRequest {
    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;
    @ApiModelProperty(value = "渠道信息", required = true)
    private String channel;
    @ApiModelProperty(value = "skuId", required = true)
    private Long skuId;
    @ApiModelProperty(value = "quoteId", required = true)
    private String quoteId;
    @ApiModelProperty(value = "商家ID", required = true)
    private String sellerId;
    @ApiModelProperty(value = "城市code", required = true)
    private String cityCode;

    @ApiModelProperty("登录用户ID")
    public Long getCustId() {
        CustDTO user = UserHolder.getUser();
        return user == null ? null : user.getCustId();
    }
    public void setCustId(Long v) { }
    @ApiModelProperty("优惠来源")
    private String promotionSource;

    @Override
    public void checkInput() {
        super.checkInput();
//        ParamUtil.nonNull(itemId, I18NMessageUtils.getMessage("product")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品ID不能为空"
//        ParamUtil.expectTrue(itemId > 0L, I18NMessageUtils.getMessage("product")+"ID"+I18NMessageUtils.getMessage("not.correct"));  //# "商品ID不正确"
        ParamUtil.nonNull(skuId, I18NMessageUtils.getMessage("sku")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品ID不能为空"
        ParamUtil.expectTrue(skuId > 0L, I18NMessageUtils.getMessage("product")+" [ID] "+I18NMessageUtils.getMessage("not.correct"));  //# "商品ID不正确"

    }
}
