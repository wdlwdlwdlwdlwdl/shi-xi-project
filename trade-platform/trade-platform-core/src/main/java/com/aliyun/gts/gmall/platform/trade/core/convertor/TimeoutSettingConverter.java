package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.TimeoutSettingQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.TimeoutSettingRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TimeoutSettingDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcTimeoutSettingDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TimeoutSetting;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TimeoutSettingQuery;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface TimeoutSettingConverter {


    TcTimeoutSettingDO toTimeoutSettingDO(TimeoutSettingDTO dto);

    TimeoutSettingDTO toTimeoutSettingDTO(TcTimeoutSettingDO timeoutSettingDO);

    TcTimeoutSettingDO toTimeoutSettingDO(TimeoutSettingRpcReq rpc);

    List<TimeoutSettingDTO> toTimeoutSettingDTOList(List<TcTimeoutSettingDO> list);

    PageInfo<TimeoutSettingDTO> toTimeoutSettingDTOPage(PageInfo<TcTimeoutSettingDO> list);

    TimeoutSetting toTimeoutSetting(TimeoutSettingQueryRpcReq rpc);

    TimeoutSettingQuery rpcToTimeoutSetting(TimeoutSettingRpcReq rpc);
}
