package com.aliyun.gts.gmall.center.trade.matchall.ext;


import com.aliyun.gts.gmall.center.trade.core.extension.common.CommonPriceCalcExt;
import com.aliyun.gts.gmall.center.trade.matchall.util.MatchAllFilter;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.ExtensionFilterContext;
import com.aliyun.gts.gmall.platform.trade.core.extension.price.PriceCalcExt;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

/**
 * 价格计算
 */
@Slf4j
@Extension(points = {PriceCalcExt.class})
public class MatchAllPriceCalcExt extends CommonPriceCalcExt {

    @Override
    public boolean filter(ExtensionFilterContext context) {
        return MatchAllFilter.filter(context);
    }
}
