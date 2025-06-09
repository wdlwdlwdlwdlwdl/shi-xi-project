package com.aliyun.gts.gmall.center.trade.normal;

import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.framework.extensionengine.ext.BizCodeParser;
import com.aliyun.gts.gmall.framework.extensionengine.ext.GmallPlugin;
import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.BizPlugin;
import org.pf4j.PluginWrapper;

@BizPlugin(bizCode = ExtBizCode.NORMAL_TRADE, name = "基础交易", description = "基础交易")
public class NormalPlugin extends GmallPlugin {

    private BizCodeParser bizCodeParser = new NormalCodeParser();

    public NormalPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public BizCodeParser getBizCodeParser() {
        return bizCodeParser;
    }
}
