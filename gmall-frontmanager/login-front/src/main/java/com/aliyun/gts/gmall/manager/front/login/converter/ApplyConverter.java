package com.aliyun.gts.gmall.manager.front.login.converter;

import com.aliyun.gts.gmall.manager.front.login.dto.input.command.ApplyRestCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.common.CustomerApplyExtendDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApplyConverter {

    CustomerApplyExtendDTO convert(ApplyRestCommand source);
}
