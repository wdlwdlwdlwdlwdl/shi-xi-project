package com.aliyun.gts.gmall.platform.trade.persistence.rpc.converter;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.TimeoutSettingRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TimeoutSettingDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcTimeoutSettingDO;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface TimeoutConverter {


    TcTimeoutSettingDO toTimeoutSettingDO(TimeoutSettingDTO dto);

    TimeoutSettingDTO toTimeoutSettingDTO(TcTimeoutSettingDO timeoutSettingDO);

    TcTimeoutSettingDO toTimeoutSettingDO(TimeoutSettingRpcReq rpc);

    List<TimeoutSettingDTO> toTimeoutSettingDTOList(List<TcTimeoutSettingDO> list);
}
