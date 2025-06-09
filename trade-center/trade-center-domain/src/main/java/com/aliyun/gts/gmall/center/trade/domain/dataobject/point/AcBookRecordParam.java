package com.aliyun.gts.gmall.center.trade.domain.dataobject.point;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/1/31 14:56
 */
@Data
public class AcBookRecordParam extends AbstractPageQueryRpcRequest {
    private static final long serialVersionUID = -5712928867086941850L;
    /**
     * 用户id
     */
    private Long custId;
    /**
     * 变动方向： 1-赋予 积分收入，2-使用 积分支出
     */
    private Integer changeDirection;
    /**
     * ChangeTypeEnum 积分变化类型，比如失效积分:invalidate
     */
    private Integer changeType;

    /**
     * 账户类型
     */
    private Integer accountType = 1;
    /**
     * 业务唯一id
     */
    private List<String> bizIds;
    /**
     * 排除
     */
    private Integer excludeChangeStep;
    /**
     * 逆向类型
     */
    private Integer targetChangeType;
    /**
     * 逆向业务id
     */
    private String targetBizId;

    /**
     *   保留状态  默认是0:不保留  1:保留中 2:保留取消 3:保留转正 9:保持处理中
     */
    private Integer reserveState;

    /**
     * 生效日期
     */
    private Date effectTime;
}
