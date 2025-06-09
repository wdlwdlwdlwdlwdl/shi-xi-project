package com.aliyun.gts.gmall.platform.trade.common.domain.step;

import com.aliyun.gts.gmall.platform.trade.common.domain.DynamicObject;

public class FormAction extends DynamicObject {

    public String getName() {
        return getObject("name");
    }
}
