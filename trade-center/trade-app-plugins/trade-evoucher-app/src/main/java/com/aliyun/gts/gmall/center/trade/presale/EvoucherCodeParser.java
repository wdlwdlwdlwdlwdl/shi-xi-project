package com.aliyun.gts.gmall.center.trade.presale;

import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.framework.extensionengine.ext.BizCodeParser;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;

public class EvoucherCodeParser implements BizCodeParser<BizCodeEntity> {

    @Override
    public boolean match(BizCodeEntity bizCodeEntity) {
        return ExtBizCode.EVOUCHER.equalsIgnoreCase(bizCodeEntity.getBizCode());
    }

    @Override
    public String getBizCode(BizCodeEntity bizCodeEntity) {
        return ExtBizCode.EVOUCHER;
    }
}
