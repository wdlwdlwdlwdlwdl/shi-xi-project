package com.aliyun.gts.gmall.center.trade.matchall.ext;

import com.aliyun.gts.gmall.center.trade.core.extension.common.CommonOrderBizCheckExt;
import com.aliyun.gts.gmall.center.trade.matchall.util.MatchAllFilter;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.ExtensionFilterContext;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizCheckExt;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

@Slf4j
@Extension(points = {OrderBizCheckExt.class})
public class MatchAllOrderBizCheckExt extends CommonOrderBizCheckExt {

    @Override
    public boolean filter(ExtensionFilterContext context) {
        return MatchAllFilter.filter(context);
    }

}
