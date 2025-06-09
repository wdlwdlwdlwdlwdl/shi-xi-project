package com.aliyun.gts.gmall.platform.trade.core.component;

import com.aliyun.gts.gmall.middleware.logistics.LogisticsComponent;
import com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsFlowDTO;
import com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsQueryReq;
import com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsTraceDTO;
import com.aliyun.gts.gmall.middleware.logistics.impl.KdnLogisticsComponent;
import com.google.common.collect.Lists;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@ConditionalOnExpression("'${logistics.trace.platform:}'.equals('mock')")
public class MockKdnLogisticsComponent extends KdnLogisticsComponent {
    @Override
    public List<LogisticsFlowDTO> queryTrace(LogisticsQueryReq req) {

        LogisticsFlowDTO l1 = new LogisticsFlowDTO();
        l1.setProcessDesc("Package Shipped from Warehouse(快递已由仓库发出)");
        l1.setRemark("Dispatched(已发出)");
        l1.setProcessTime("2022-12-12 00:00:00");

        LogisticsFlowDTO l2 = new LogisticsFlowDTO();
        l2.setProcessDesc("Package Arrived at Designated Area(快递已到达指定地区)");
        l2.setRemark("Arrived(已到达)");
        l2.setProcessTime("2022-12-13 11:00:00");

        LogisticsFlowDTO l3 = new LogisticsFlowDTO();
        l3.setProcessDesc("Package Delivered(快递已签收)");
        l3.setRemark("Arrived(已到达)");
        l3.setProcessTime("2022-12-13 18:00:00");

        List<LogisticsFlowDTO> list = Lists.newArrayList(l1,l2,l3);

        return list;
    }

    @Override
    public LogisticsTraceDTO queryTraceV2(LogisticsQueryReq req) {
        return super.queryTraceV2(req);
    }
}
