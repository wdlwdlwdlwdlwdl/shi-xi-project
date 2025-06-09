package com.aliyun.gts.gmall.platform.trade.app.example;

import com.aliyun.gts.gmall.framework.extensionengine.ext.BizCodeParser;
import com.aliyun.gts.gmall.framework.extensionengine.ext.GmallPlugin;
import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.BizPlugin;
import org.pf4j.PluginWrapper;

@BizPlugin(bizCode = ExampleConstants.BIZ_CODE, name = "业务扩展例子", description = "业务扩展例子")
public class ExamplePlugin extends GmallPlugin {

    private final BizCodeParser bizCodeParser = new ExampleCodeParser();

    public ExamplePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public BizCodeParser getBizCodeParser() {
        return bizCodeParser;
    }
}
