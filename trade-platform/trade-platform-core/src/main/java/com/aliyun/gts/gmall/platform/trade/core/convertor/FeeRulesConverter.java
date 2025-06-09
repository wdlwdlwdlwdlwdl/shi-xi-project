package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.FeeRulesDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcFeeRulesDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.DeliveryFee;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.FeeRules;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface FeeRulesConverter {


    TcFeeRulesDO toTcFeeRulesDO(FeeRulesDTO dto);

    FeeRulesDTO toFeeRulesDTO(TcFeeRulesDO rules);

    TcFeeRulesDO toTcFeeRulesDO(FeeRulesRpcReq rpc);

    PageInfo<FeeRulesDTO> toFeeRulesDTOPage(PageInfo<TcFeeRulesDO> list);

    FeeRules toTcFeeRules(FeeRulesQueryRpcReq rpc);
}
