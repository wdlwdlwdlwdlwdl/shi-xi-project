package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class OrderExtraSaveRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty(value = "主订单ID", required = true)
    private Long primaryOrderId;

    @ApiModelProperty("子订单ID (更新主订单扩展属性时传null)")
    private Long orderId;

    @ApiModelProperty("用于身份校验, 可空")
    private Long sellerId;

    @ApiModelProperty("用于身份校验, 可空")
    private Long custId;


    // =========== feature 扩展 ===========

    @ApiModelProperty("新增/更新 feature kv")
    private Map<String /* feature key */, String /* feature value */> addFeatures;

    @ApiModelProperty("移除 feature key")
    private Set<String /* feature key */> removeFeatures;


    // =========== extend 表扩展 ===========

    @ApiModelProperty("新增/更新 extend type-kv")
    private Map<String /* extend type */, Map<String /* extend key */, String /* extend value */>> addExtends;

    @ApiModelProperty("移除 extend type-key")
    private Map<String /* extend type */, Set<String> /* extend key */> removeExtendKeys;

    @ApiModelProperty("移除 extend type")
    private Set<String /* extend type */> removeExtendTypes;

    // =========== 订单标 tags (进搜索) ===========

    @ApiModelProperty("增加的tag")
    private List<String> addTags;

    @ApiModelProperty("移除的tag")
    private Set<String> removeTags;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(primaryOrderId, I18NMessageUtils.getMessage("main.order")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "主订单ID不能为空"
    }
}
