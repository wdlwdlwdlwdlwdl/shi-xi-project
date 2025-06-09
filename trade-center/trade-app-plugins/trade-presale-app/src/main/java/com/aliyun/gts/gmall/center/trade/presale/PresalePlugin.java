package com.aliyun.gts.gmall.center.trade.presale;

import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.framework.extensionengine.ext.BizCodeParser;
import com.aliyun.gts.gmall.framework.extensionengine.ext.GmallPlugin;
import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.BizPlugin;
import org.pf4j.PluginWrapper;

@BizPlugin(bizCode = ExtBizCode.PRE_SALE, name = "预售", description = "预售")
public class PresalePlugin extends GmallPlugin {

    private BizCodeParser bizCodeParser = new PresaleCodeParser();

    public PresalePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public BizCodeParser getBizCodeParser() {
        return bizCodeParser;
    }
}
