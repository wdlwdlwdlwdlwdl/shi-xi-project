package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

import java.util.Date;

/**
 * 订单定时任务
 */
@Data
public class OrderTaskDTO extends AbstractOutputInfo {
    public static final String TASK_TYPE_SYS_CONFIRM = "SystemConfirmOrderTask";
    public static final String TASK_TYPE_CLOSE_UNPAID = "CloseUnpaidOrderTask";


    public static final int STATUS_WAIT = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_FINISH = 3;
    public static final int STATUS_DELETE = -1;

    /**
     * 主键
     */
    private Long id;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 任务状态, -1:逻辑删除, 1:未执行, 2:异常, 3:完成
     */
    private Integer taskStatus;

    /**
     * 任务参数
     */
    private String taskParams;

    /**
     * 关联的主订单ID
     */
    private Long primaryOrderId;

    /**
     * 预定执行时间
     */
    private Date scheduleTime;

    /**
     * 最近一次执行结果
     */
    private String executeMessage;

    /**
     * 执行次数
     */
    private Integer executeCount;

    /**
     *
     */
    private Date gmtCreate;

    /**
     *
     */
    private Date gmtModified;

    /**
     * 版本
     */
    private Long version;
}
