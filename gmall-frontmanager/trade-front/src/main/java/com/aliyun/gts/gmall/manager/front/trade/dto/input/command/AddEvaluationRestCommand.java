package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.param.SubOrderEvaluation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 添加评价请求
 *
 * @author tiansong
 */
@ApiModel("添加评价请求")
@Data
public class AddEvaluationRestCommand extends LoginRestCommand {

    @ApiModelProperty(value = "主订单ID", required = true)
    private Long                     primaryOrderId;
    @ApiModelProperty("卖家ID")
    private Long                     sellerId;
    @ApiModelProperty(value = "服务评价分数")
    private Integer                  serviceScore;
    @ApiModelProperty(value = "服务评价描述")
    private String                  serviceDesc;
    @ApiModelProperty(value = "物流评价分数")
    private Integer                  logisticsScore;
    @ApiModelProperty(value = "物流评价描述")
    private String                  logisticsDesc;
    @ApiModelProperty(value = "子订单评价", required = true)
    private List<SubOrderEvaluation> subOrderEvaluationList;
    @ApiModelProperty(value = "是否追评")
    private Boolean additional = false;

    private String custName;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(primaryOrderId, I18NMessageUtils.getMessage("main.order")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "主订单ID不能为空"

        if (!isAdditional()) {
            ParamUtil.nonNull(serviceScore, I18NMessageUtils.getMessage("service.evaluation.required"));  //# "服务评价不能为空"
            ParamUtil.expectInRange(serviceScore, 1, 5, I18NMessageUtils.getMessage("service.evaluation.score")+"1-5分");  //# "服务评价分数
            ParamUtil.nonNull(logisticsScore, I18NMessageUtils.getMessage("logistics.evaluation.required"));  //# "物流评价不能为空"
            ParamUtil.expectInRange(logisticsScore, 1, 5, I18NMessageUtils.getMessage("logistics.evaluation.score")+"1-5分");  //# "物流评价分数
        }

        ParamUtil.notEmpty(subOrderEvaluationList, I18NMessageUtils.getMessage("sub.order.evaluation.required"));  //# "子订单评价不能为空"
        subOrderEvaluationList.forEach(subOrderEvaluation -> {
            subOrderEvaluation.checkInput();
            // fill primaryOrder info
            subOrderEvaluation.setCustId(this.getCustId());
            subOrderEvaluation.setSellerId(this.getSellerId());
            subOrderEvaluation.setPrimaryOrderId(this.getPrimaryOrderId());

            if (!isAdditional()) {
                subOrderEvaluation.checkNotAdditional();
            }
        });
    }

    public boolean isAdditional() {
        return additional != null && additional.booleanValue();
    }
}