package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirm;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmStepOrderInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmStepExtendDTO;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepTemplate;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.StepOrderDomainService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfirmStepOrderHandler extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private StepOrderDomainService stepOrderDomainService;

    @Override
    public void handle(TOrderConfirm inbound) {
        CreatingOrder c = inbound.getDomain();
        boolean hasStepOrder = false;
        for (MainOrder mainOrder : c.getMainOrders()) {
            if (StepOrderUtils.isMultiStep(mainOrder)) {
                fillRequestStepInfo(inbound.getReq().getConfirmStepOrderInfo(), mainOrder);
                stepOrderDomainService.fillStepInfoOnCreate(mainOrder);
                hasStepOrder = true;
            }
        }
        if (hasStepOrder) {
            ConfirmStepExtendDTO ext = stepOrderDomainService.calcStepExtend(c);
            c.putExtra("ConfirmStepExtendDTO", ext);
        }
    }

    private static void fillRequestStepInfo(ConfirmStepOrderInfo req, MainOrder mainOrder) {
        StepTemplate template = new StepTemplate();
        if (req == null || StringUtils.isBlank(req.getStepTemplateName())) {
            template.setTemplateName("preSale");
            mainOrder.setStepTemplate(template);
            return;
        }
//        StepTemplate template = new StepTemplate();
        template.setTemplateName(req.getStepTemplateName());
        mainOrder.setStepTemplate(template);

        mainOrder.orderAttr().setStepContextProps(req.getStepContextProps());
        mainOrder.orderAttr().setStepTemplateName(req.getStepTemplateName());
    }
}
