package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.middleware.logistics.LogisticsComponent;
import com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsFlowDTO;
import com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsQueryReq;
import com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsTraceDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockLogisticsComponent implements LogisticsComponent {
    @Override
    public List<LogisticsFlowDTO> queryTrace(LogisticsQueryReq req) {
        return null;
    }

    @Override
    public LogisticsTraceDTO queryTraceV2(LogisticsQueryReq req) {
        return null;
    }
}
