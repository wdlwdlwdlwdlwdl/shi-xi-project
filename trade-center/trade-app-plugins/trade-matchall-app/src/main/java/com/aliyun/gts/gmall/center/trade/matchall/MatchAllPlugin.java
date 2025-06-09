package com.aliyun.gts.gmall.center.trade.matchall;

import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.framework.extensionengine.ext.BizCodeParser;
import com.aliyun.gts.gmall.framework.extensionengine.ext.GmallPlugin;
import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.BizPlugin;
import org.pf4j.PluginWrapper;

@BizPlugin(bizCode = ExtBizCode.MATCH_ALL, name = "兜底产品包", description = "兜底产品包")
public class MatchAllPlugin extends GmallPlugin {

    private BizCodeParser bizCodeParser = new MatchAllCodeParser();

    public MatchAllPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public BizCodeParser getBizCodeParser() {
        return bizCodeParser;
    }
}
