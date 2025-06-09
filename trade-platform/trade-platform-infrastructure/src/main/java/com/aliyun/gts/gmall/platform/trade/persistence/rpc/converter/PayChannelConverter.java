package com.aliyun.gts.gmall.platform.trade.persistence.rpc.converter;

import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.PayChannelDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.PayChannel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PayChannelConverter {

    public abstract List<PayChannel> toPayChannels(List<PayChannelDTO> list);
}
