package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirmv2;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmStepOrderInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmStepExtendDTO;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepTemplate;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.StepOrderDomainService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 订单确认 step8
 *    多步骤订单计算
 *    预售场景使用，先下单定金 再付尾款
 *    读取
 * 2024-12-11 14:34:19
 */
@Slf4j
@Component
public class ConfirmStepOrderNewHandler extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private StepOrderDomainService stepOrderDomainService;

    @Override
    public void handle(TOrderConfirm inbound) {
        CreatingOrder creatingOrder = inbound.getDomain();
        boolean hasStepOrder = false;
        for (MainOrder mainOrder : creatingOrder.getMainOrders()) {
            if (StepOrderUtils.isMultiStep(mainOrder)) {
                //多步骤参数 读取配置信息
                fillRequestStepInfo(inbound.getReq().getConfirmStepOrderInfo(), mainOrder);
                // 填充分步订单计算
                stepOrderDomainService.fillStepInfoOnCreate(mainOrder);
                hasStepOrder = true;
            }
        }
        // 是分步骤订单
        if (Boolean.TRUE.equals(hasStepOrder)) {
            // 分步订单计算
            ConfirmStepExtendDTO confirmStepExtendDTO = stepOrderDomainService.calcStepExtend(creatingOrder);
            creatingOrder.putExtra("ConfirmStepExtendDTO", confirmStepExtendDTO);
        }
    }

    /**
     * 多步骤参数 读取配置信息
     * @param req
     * @param mainOrder
     */
    private static void fillRequestStepInfo(ConfirmStepOrderInfo req, MainOrder mainOrder) {
        StepTemplate template = new StepTemplate();
        if (Objects.isNull(req) || StringUtils.isBlank(req.getStepTemplateName())) {
            log.info("req.getStepTemplateName is null ");
            template.setTemplateName("preSale");
            mainOrder.setStepTemplate(template);
            mainOrder.orderAttr().setStepTemplateName(template.getTemplateName());
            return;
        }
        log.info("req.getStepTemplateName is not null = {}", req.getStepTemplateName());
        template.setTemplateName(req.getStepTemplateName());
        mainOrder.setStepTemplate(template);
        mainOrder.orderAttr().setStepContextProps(req.getStepContextProps());
        mainOrder.orderAttr().setStepTemplateName(req.getStepTemplateName());
    }
}
