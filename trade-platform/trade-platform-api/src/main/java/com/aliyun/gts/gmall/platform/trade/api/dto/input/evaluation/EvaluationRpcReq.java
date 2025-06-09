package com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiModel("提交评论")
@Data
public class EvaluationRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty("评价分数")
    Integer rateScore;

    @ApiModelProperty("评价描述")
    @Size(max = 1024, message = "rateDesc.max")
    String rateDesc;

    @ApiModelProperty("评价图片")
    @Size(max = 10)
    List<String> ratePic;

    @ApiModelProperty("评价视频")
    @Size(max = 10)
    List<String> rateVideo;

    @ApiModelProperty("主订单id")
    @NotNull
    Long primaryOrderId;

    @ApiModelProperty("子订单id")
    @NotNull
    Long orderId;

    @ApiModelProperty("商品id")
    @NotNull
    Long itemId;

    Long skuId;

    @ApiModelProperty("买家id")
    @NotNull
    Long custId;

    @ApiModelProperty("买家名称")
    String custName;

    @ApiModelProperty("卖家id")
    @NotNull
    Long sellerId;

    @ApiModelProperty("卖家名称")
    String sellerName;

    @ApiModelProperty("卖家bin/iin")
    String binOrIin;

    @ApiModelProperty("扩展内容")
    Map extend;

    @NotNull
    @ApiModelProperty("被回复的评论id")
    Long replyId;

    private String itemTitle;

    public Map extend() {
        if (extend == null) {
            extend = new HashMap();
        }
        return extend;
    }
}
