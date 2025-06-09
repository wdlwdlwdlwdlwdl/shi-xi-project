package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.framework.domain.extend.service.DomainExtendService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MockDomainExtendService extends DomainExtendService {

    public Map<String, String> loadAllExtendsWithCheck(Map<String, String> extendAdds) {
        return extendAdds;
    }
}