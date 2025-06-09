package com.aliyun.gts.gmall.center.trade.matchall;

import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.framework.extensionengine.ext.BizCodeParser;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;

public class MatchAllCodeParser implements BizCodeParser<BizCodeEntity> {

    @Override
    public boolean match(BizCodeEntity bizCodeEntity) {
        return true;
    }

    @Override
    public String getBizCode(BizCodeEntity bizCodeEntity) {
        return ExtBizCode.MATCH_ALL;
    }
}
