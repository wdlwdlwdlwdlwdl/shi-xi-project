package com.aliyun.gts.gmall.center.trade.matchall.ext;

import com.aliyun.gts.gmall.center.trade.core.domainservice.CombineItemService;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.ExtensionFilterContext;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.ReversalFeeExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.defaultimpl.DefaultReversalFeeExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/25 16:19
 */
@Slf4j
@Extension(points = {ReversalFeeExt.class})
public class MatchAllReversalFeeExt extends DefaultReversalFeeExt {

    @Autowired
    private CombineItemService combineItemService;

    @Override
    public boolean filter(ExtensionFilterContext context) {
        //return MatchAllFilter.filter(context);
        return super.filter(context);   // 此扩展点不排他
    }

   /* @Override
    protected void reversalBizCheck(MainReversal reversal, List<MainReversal> historyList) {
        // combineItemService.checkReversal(reversal,historyList);
    }*/
}
