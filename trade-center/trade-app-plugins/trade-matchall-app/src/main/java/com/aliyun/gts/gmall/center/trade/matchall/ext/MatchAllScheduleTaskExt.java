package com.aliyun.gts.gmall.center.trade.matchall.ext;

import com.aliyun.gts.gmall.center.trade.core.extension.common.CommonScheduleTaskExt;
import com.aliyun.gts.gmall.center.trade.matchall.util.MatchAllFilter;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.ExtensionFilterContext;
import com.aliyun.gts.gmall.platform.trade.core.extension.task.ScheduleTaskExt;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;


@Slf4j
@Extension(points = {ScheduleTaskExt.class})
public class MatchAllScheduleTaskExt extends CommonScheduleTaskExt {

    @Override
    public boolean filter(ExtensionFilterContext context) {
        return MatchAllFilter.filter(context);
    }
}
