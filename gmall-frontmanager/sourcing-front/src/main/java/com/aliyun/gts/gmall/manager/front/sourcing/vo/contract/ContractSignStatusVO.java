package com.aliyun.gts.gmall.manager.front.sourcing.vo.contract;

import javax.validation.constraints.NotNull;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import lombok.Data;

@Data
public class ContractSignStatusVO extends LoginRestCommand {

    @NotNull
    Long id;

    Long  signStatus;
}
