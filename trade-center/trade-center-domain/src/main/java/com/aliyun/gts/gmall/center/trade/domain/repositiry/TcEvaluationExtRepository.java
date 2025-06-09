package com.aliyun.gts.gmall.center.trade.domain.repositiry;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;

/**
 * @author : zhang.beilei
 * @date : 2022/10/18 17:31
 **/

public interface TcEvaluationExtRepository {

    TcEvaluationDO getById(Long id);

    Boolean updateExtend(Long id,String extendStr);

    Boolean updateExtendByPrimaryOrderId(Long id,String extendStr);
}
