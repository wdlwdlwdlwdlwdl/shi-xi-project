package com.aliyun.gts.gmall.center.trade.presale;

import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.framework.extensionengine.ext.BizCodeParser;
import com.aliyun.gts.gmall.framework.extensionengine.ext.GmallPlugin;
import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.BizPlugin;
import org.pf4j.PluginWrapper;

@BizPlugin(bizCode = ExtBizCode.EVOUCHER, name = "电子凭证", description = "电子凭证")
public class EvoucherPlugin extends GmallPlugin {

    private BizCodeParser bizCodeParser = new EvoucherCodeParser();

    public EvoucherPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public BizCodeParser getBizCodeParser() {
        return bizCodeParser;
    }
}
