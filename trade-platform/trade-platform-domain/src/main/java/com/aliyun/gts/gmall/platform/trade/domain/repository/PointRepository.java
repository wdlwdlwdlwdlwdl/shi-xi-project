package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointReduceParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointRollbackParam;

import java.util.List;

public interface PointRepository {

    /**
     * 查询用户可用积分
     */
    Long getUserPoint(Long custId);

    /**
     * 锁积分, 事务性, 全部成功或失败
     */
    void lockPoint(List<PointReduceParam> param);

    /**
     * 解锁积分
     */
    void unlockPoint(List<PointReduceParam> param);

    /**
     * 退积分
     */
    //void rollbackPoint(List<PointRollbackParam> param);

    /**
     * 锁定之后的扣减积分、注意：直接扣减不要调用此接口
     */
    //boolean deductPointsAfterLock(PointReduceParam pointReduceParam);
}
