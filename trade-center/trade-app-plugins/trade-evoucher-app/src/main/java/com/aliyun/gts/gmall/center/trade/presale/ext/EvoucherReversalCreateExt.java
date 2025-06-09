package com.aliyun.gts.gmall.center.trade.presale.ext;

import com.aliyun.gts.gmall.center.trade.core.extension.common.CommonReversalCreateExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.ReversalCreateExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

/**
 * 电子凭证售后创建
 * 1. needLogistics 标为 false : 不需要物流
 */
@Slf4j
@Extension(points = {ReversalCreateExt.class})
public class EvoucherReversalCreateExt extends CommonReversalCreateExt {

    @Override
    public void beforeSave(MainReversal mainReversal) {
        super.beforeSave(mainReversal);
        mainReversal.reversalFeatures().setNeedLogistics(false);
    }
}
