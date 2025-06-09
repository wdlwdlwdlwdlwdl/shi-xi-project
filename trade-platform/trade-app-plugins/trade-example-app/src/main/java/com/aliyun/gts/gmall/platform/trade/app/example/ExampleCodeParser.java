package com.aliyun.gts.gmall.platform.trade.app.example;

import com.aliyun.gts.gmall.framework.extensionengine.ext.BizCodeParser;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;

public class ExampleCodeParser implements BizCodeParser<BizCodeEntity> {
    @Override
    public boolean match(BizCodeEntity bizCodeEntity) {
        return ExampleConstants.BIZ_CODE.equalsIgnoreCase(bizCodeEntity.getBizCode())
                && ExampleConstants.ORDER_TYPE_CODE.equalsIgnoreCase(bizCodeEntity.getOrderType());
    }

    @Override
    public String getBizCode(BizCodeEntity bizCodeEntity) {
        return ExampleConstants.BIZ_CODE;
    }
}
