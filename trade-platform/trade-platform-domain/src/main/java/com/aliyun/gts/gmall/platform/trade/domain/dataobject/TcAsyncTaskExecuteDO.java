package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tc_async_task_execute")
public class TcAsyncTaskExecuteDO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 创建（执行）时间
     */
    private Date gmtCreate;

    /**
     * 1:成功, 2:失败
     */
    private Boolean success;

    /**
     * 结果内容
     */
    private String resultMessage;
}
