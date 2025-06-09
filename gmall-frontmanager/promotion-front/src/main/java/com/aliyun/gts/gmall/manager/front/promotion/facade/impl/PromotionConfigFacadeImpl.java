package com.aliyun.gts.gmall.manager.front.promotion.facade.impl;

import com.aliyun.gts.gmall.manager.front.promotion.adaptor.PromotionApiAdaptor;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PromotionConfigVO;
import com.aliyun.gts.gmall.manager.front.promotion.facade.PromotionConfigFacade;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.GrantIntegralConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PromotionConfigFacadeImpl implements PromotionConfigFacade {

    @Autowired
    private PromotionApiAdaptor promotionApiAdaptor;



    @Override
    public PromotionConfigVO evaluationPoint() {
        GrantIntegralConfigDTO grantIntegralConfigDTO = promotionApiAdaptor.queryConfig();
        if (null != grantIntegralConfigDTO) {
            PromotionConfigVO promotionConfigVO = new PromotionConfigVO();
            promotionConfigVO.setEvaluationPointSwitch(grantIntegralConfigDTO.getEvaluationPointSwitch());
            promotionConfigVO.setEvaluationPointCount(grantIntegralConfigDTO.getEvaluationPointCount());
            return promotionConfigVO;
        }
        return null;
    }
}
