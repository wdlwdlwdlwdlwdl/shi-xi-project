package com.aliyun.gts.gmall.manager.front.item.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.Map;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 单个商品详情页多选sku计算价格请求
 *
 * @author linyi
 */
@Data
@ApiModel("单个商品详情页多选sku计算价格请求")
public class ItemPriceRestQuery extends LoginRestQuery {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;

    @ApiModelProperty("sku价格和数量,可能包括数量为0的")
    private Map<Long, Long> skuMap;

    @Override
    public void checkInput() {

        ParamUtil.nonNull(itemId, I18NMessageUtils.getMessage("product")+" [id] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品id不能为空"
        ParamUtil.expectTrue(skuMap != null && skuMap.size() > 0, I18NMessageUtils.getMessage("specification.not.selected"));  //# "未选中规格"

    }
}
