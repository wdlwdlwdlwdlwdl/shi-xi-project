package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationQueryRpcReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class EvaluationQueryReq extends EvaluationQueryRpcReq {
    //订单ID  长度小于32
    @ApiModelProperty(value = "订单ID")
    private Long orderNumber;

    //商品名称  长度小于32
    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品星级")
    private String rateScore;

    //评论状态  下拉多选
    @ApiModelProperty(value = "评论状态")
    private String status;

    //评论时间
    @ApiModelProperty(value = "评论时间区间")
    private List<Date> evaluateTime;

    private boolean hasItemId;

    /**
     * 前端参数 ==> rpc参数
     */
    public void build() {
        if (evaluateTime != null && evaluateTime.size() == 2) {
            super.setEvaluateTimeStart(evaluateTime.get(0));
            super.setEvaluateTimeEnd(evaluateTime.get(1));
        }
        //订单ID  长度小于32
        if (orderNumber != null && orderNumber.toString().length() <= 32) {
            super.setSubOrderId(orderNumber);
        }
        //商品名称  长度小于32
        if (productName != null && productName.length() <= 32) {
            super.setItemTitle(productName);
        }

//        if (rateScore != null) {
//            super.setRateScoreMax(rateScore);
//            super.setRateScoreMin(rateScore);
//        }
        //评论分数  下拉多选
        if (rateScore!=null) {
            String[] productRatingArrays = rateScore.split(",");
            super.setProductRatingArrays(productRatingArrays);
        }

        //评论状态  下拉多选  EvaluationApproveStatusEnum
        if (status!=null) {
            String[] statusArrays = status.split(",");
            super.setApprovalStatus(statusArrays);
        }
    }

    private Map<String, List<Object>> extraFilters() {
        Map<String, List<Object>> extraFilters = super.getExtraFilters();
        if (extraFilters == null) {
            extraFilters = new HashMap<>();
            super.setExtraFilters(extraFilters);
        }
        return extraFilters;
    }

}
