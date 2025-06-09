package com.aliyun.gts.gmall.center.trade.domain.repositiry;

import com.aliyun.gts.gmall.center.trade.domain.dataobject.point.AcBookOrderRecordDO;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.point.AcBookRecordDO;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.point.AcBookRecordParam;
import com.aliyun.gts.gmall.center.trade.domain.entity.point.PointGrantConfig;
import com.aliyun.gts.gmall.center.trade.domain.entity.point.PointGrantParam;
import com.aliyun.gts.gmall.center.trade.domain.entity.point.PointRollbackExtParam;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointRollbackParam;

import java.util.Date;

public interface PointGrantRepository {

    /**
     * 获取积分赠送配置
     */
    PointGrantConfig getGrantConfig();

    /**
     * 根据id查询积分赠送记录
     * @param id
     * @return
     */
    AcBookRecordDO queryGrantRecord(Long id);

    /**
     * 根据赠送记录的保留状态
     * @param id
     * @param oldReserveState
     * @return
     */
    boolean updateGrantRecordReserveState(Long id, Integer oldReserveState, Integer newReserveState);

    /**
     * 分页查询积分赠送记录
     * @param query
     * @return
     */
    PageInfo<AcBookRecordDO> queryGrantRecord(AcBookRecordParam query);


    /**
     * 分页查询积分赠送记录
     * @param query
     * @return
     */
    PageInfo<AcBookOrderRecordDO> queryGrantOrderRecord(AcBookRecordParam query);

    /**
     * 计算积分失效日期
     */
    Date calcInvalidDate(PointGrantConfig config, Date startDate);

    /**
     * 赠送积分
     */
    void grantPoint(PointGrantParam param);

    /**
     * 退回赠送积分
     */
    void rollbackGrantPoint(PointRollbackParam param);

    /**
     * 退回赠送积分, 不退成负数
     */
    long rollbackGrantPointPositive(PointRollbackExtParam param);

}
