package com.aliyun.gts.gmall.center.trade.deposit;

import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.framework.extensionengine.ext.BizCodeParser;
import com.aliyun.gts.gmall.framework.extensionengine.ext.GmallPlugin;
import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.BizPlugin;
import org.pf4j.PluginWrapper;

@BizPlugin(bizCode = ExtBizCode.DEPOSIT, name = "定金尾款", description = "定金尾款")
public class DepositPlugin extends GmallPlugin {

    private BizCodeParser bizCodeParser = new DepositCodeParser();

    public DepositPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public BizCodeParser getBizCodeParser() {
        return bizCodeParser;
    }
}
