package com.aliyun.gts.gmall.platform.trade.core.extension.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmStepExtendDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import lombok.Data;

public interface StepOrderCreateExt extends IExtensionPoints {

    /**
     * 获取多阶段模版, 并填入 mainOrder
     */
    void fillStepTemplate(MainOrder mainOrder);

    /**
     * 解析模版, 填入多阶段订单
     */
    void fillStepOrders(MainOrder mainOrder);

    /**
     * 计算扩展信息 (firstPay / remainPay)
     */
    ConfirmStepExtendDTO calcStepExtend(CreatingOrder c);

    /**
     * 分摊阶段价格到子订单
     */
    void divideStepPriceToSubOrder(StepOrder step, MainOrder mainOrder, DivideStepPriceOption opt);

    @Data
    class DivideStepPriceOption {
        boolean adjFee; // 是否改价
        boolean lastModifiedStep; // 是否最后一个待计算的step, 最后一个为减法
    }
}
