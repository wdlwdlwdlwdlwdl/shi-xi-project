package com.aliyun.gts.gmall.platform.trade.common.domain.step;

import com.aliyun.gts.gmall.platform.trade.common.domain.DynamicObject;


public class FormData extends DynamicObject {

    public String getName() {
        return getObject("name");
    }

    public String getTitle() {
        return getObject("title");
    }

    public String getType() {
        return getObject("type");
    }
}
