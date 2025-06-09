package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderChangeNotify {

    /**
     * orderList / mainOrder 二选一
     */
    private List<TcOrderDO> orderList;

    /**
     * orderList / mainOrder 二选一
     */
    private MainOrder mainOrder;

    /**
     * 可选参数
     */
    private List<TcOrderOperateFlowDO> flows;

    /**
     * 操作类型
     */
    private OrderChangeOperate op;

    private Integer fromOrderStatus;

    private Integer cancelFromStatus;
}
