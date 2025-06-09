package com.aliyun.gts.gmall.manager.front.trade.component.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aliyun.gts.gmall.manager.front.trade.annotation.VoOrderType;
import com.aliyun.gts.gmall.manager.front.trade.component.OrderExtendVOBuildComponent;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ConfirmOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.Result;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.AddressVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.OrderExtendVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.SkuExtendVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.NonblockFailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
/**
 * OrderTypeEnum.PHYSICAL_GOODS
 */
@VoOrderType(1)
public class NormalExtendVOBuildComponentImpl implements OrderExtendVOBuildComponent {

    private static final String CONFIRM_ADDRESS_CODE = "20110015";

    @Override
    public OrderExtendVO buildExtend(MainOrderDetailDTO mainOrderDetailDTO) {
        return null;
    }

    @Override
    public OrderExtendVO buildExtend(SubOrderDTO subOrderDTO , MainOrderDetailDTO mainOrderDetailDTO) {
        SkuExtendVO skuExtendVO = new SkuExtendVO();
        skuExtendVO.setSkuId(subOrderDTO.getSkuId());
//        skuExtendVO.setItemFeature(subOrderDTO.getItemFeature());
        skuExtendVO.setSkuDesc(subOrderDTO.getSkuDesc());
        skuExtendVO.setWeight(subOrderDTO.getWeight());
        skuExtendVO.setOrderType(OrderTypeEnum.PHYSICAL_GOODS.getCode());
        return skuExtendVO;
    }

    @Override
    public Result<OrderExtendVO> buildExtend(ConfirmOrderRestQuery query,ConfirmOrderDTO confirmOrderDTO) {
        Result<OrderExtendVO> result = new Result<>();
//        result.setSuccess(this.checkAddress(query.getAddressVO(), confirmOrderDTO));
        // 设置收货地址信息
//        AddressVO orderExtendVO = (query.getAddressVO() == null || query.getAddressVO().getId() == null) ? new AddressVO() : query.getAddressVO();
//        result.setErrorMsg(result.isSuccess() ? null : ((query.getAddressVO() == null || query.getAddressVO().getId() == null) ?
//                I18NMessageUtils.getMessage("add.delivery.address") : TradeFrontResponseCode.ORDER_CONFIRM_ADDRESS_ERROR.getMessage()));  //# "请添加收货地址"
//        orderExtendVO.setOrderType(OrderTypeEnum.PHYSICAL_GOODS.getCode());
//        result.setData(orderExtendVO);
        return result;
    }

    /**
     * 设置收货信息；未设置默认收货地址或不支持配送，则需要清空收货地址，并提醒用户设置
     *
     * @param addressVO
     * @param confirmOrderDTO
     * @return
     */
    private Boolean checkAddress(AddressVO addressVO, ConfirmOrderDTO confirmOrderDTO) {
        // 无默认地址信息
        if (addressVO == null || addressVO.getId() == null) {
            return Boolean.FALSE;
        }
        // 含非阻塞性错误，并且是商品无法配送至该区域
        if (CollectionUtils.isNotEmpty(confirmOrderDTO.getNonblockFails())) {
            List<NonblockFailDTO> nonBlockFailDTOList = confirmOrderDTO.getNonblockFails().stream().filter(
                nonBlockFailDTO -> CONFIRM_ADDRESS_CODE.equals(nonBlockFailDTO.getCode())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(nonBlockFailDTOList)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public Map<Long, OrderExtendVO> buildExtendList(Collection<MainOrderDTO> orderList) {
        return null;
    }
}
