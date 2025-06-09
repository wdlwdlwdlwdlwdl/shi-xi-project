package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import lombok.Data;

@Data
public class IdQuery extends LoginRestCommand {
    Long id;
}
