package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tc_step_template")
public class TcStepTemplateDO implements Serializable {
    private static final long serialVersionUID=1L;

    private Long id;

    private String name;

    private String content;
}
