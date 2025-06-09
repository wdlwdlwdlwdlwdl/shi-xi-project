package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcStepTemplateDO;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepTemplate;

public interface TcStepTemplateRepository {

    TcStepTemplateDO queryByName(String name);

    StepTemplate queryParsedTemplateWithCache(String name);

}
