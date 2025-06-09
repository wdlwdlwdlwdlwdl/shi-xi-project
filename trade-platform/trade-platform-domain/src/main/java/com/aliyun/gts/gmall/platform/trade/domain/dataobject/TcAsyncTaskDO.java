package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tc_async_task")
public class TcAsyncTaskDO {
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
