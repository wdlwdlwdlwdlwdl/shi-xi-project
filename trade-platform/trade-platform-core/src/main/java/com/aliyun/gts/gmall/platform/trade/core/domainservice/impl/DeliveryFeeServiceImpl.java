package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.MerchantDeliveryFeeEnum;
import com.aliyun.gts.gmall.platform.trade.core.convertor.DeliveryFeeConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.DeliveryFeeService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.DeliveryFeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DeliveryFeeServiceImpl implements DeliveryFeeService {

    @Autowired
    private DeliveryFeeRepository deliveryFeeRepository;

    @Autowired
    DeliveryFeeConverter deliveryFeeConverter;

    /**
     * 查询卖家运费列表
     * @param req
     * @return
     */
    @Override
    public PageInfo<DeliveryFeeDTO> queryDeliveryFee(DeliveryFeeQueryRpcReq req) {
        PageInfo<TcDeliveryFeeDO> list = deliveryFeeRepository.queryDeliveryFeeList(deliveryFeeConverter.toTcDeliveryFee(req));
        return deliveryFeeConverter.toDeliveryFeeDTOPage(list);
    }

    /**
     * 保存卖家运费
     * @param req
     * @return
     */
    @Override
    public DeliveryFeeDTO saveDeliveryFee(DeliveryFeeRpcReq req) {
        TcDeliveryFeeDO deliveryFeeDO = deliveryFeeConverter.toTcDeliveryFeeDO(req);
        // 先查询一下
        List<TcDeliveryFeeDO> tcDeliveryFeeList = deliveryFeeRepository.checkExist(deliveryFeeDO);
        if (CollectionUtils.isNotEmpty(tcDeliveryFeeList)) {
            throw new GmallException(CommonResponseCode.AlreadyExists);
        }
        // 保存物流费用
        return deliveryFeeConverter.toDeliveryFeeDTO(deliveryFeeRepository.saveDeliveryFee(deliveryFeeDO));
    }

    /**
     * 保存卖家运费
     * @param req
     * @return
     */
    @Override
    public DeliveryFeeDTO updateDeliveryFee(DeliveryFeeRpcReq req) {
        TcDeliveryFeeDO deliveryFeeDO = deliveryFeeConverter.toTcDeliveryFeeDO(req);
        if (Objects.nonNull(req.getIsCategoryAll()) ||
            Objects.nonNull(req.getIsMerchantAll())) {
            // 先查询一下
            List<TcDeliveryFeeDO> tcDeliveryFeeList = deliveryFeeRepository.checkExist(deliveryFeeDO);
            if (CollectionUtils.isNotEmpty(tcDeliveryFeeList)) {
                TcDeliveryFeeDO check = tcDeliveryFeeList.stream()
                    .filter(Objects::nonNull)
                    .filter(tcDeliveryFeeDO -> !tcDeliveryFeeDO.getId().equals(req.getId()))
                    .findFirst()
                    .orElse(null);
                if (Objects.nonNull(check)) {
                    throw new GmallException(CommonResponseCode.AlreadyExists);
                }
            }
        }
        return deliveryFeeConverter.toDeliveryFeeDTO(deliveryFeeRepository.updateDeliveryFee(deliveryFeeDO));
    }

    /**
     * 卖家运费详情
     * @param req
     * @return
     */
    @Override
    public DeliveryFeeDTO deliveryFeeDetail(DeliveryFeeRpcReq req) {
        TcDeliveryFeeDO deliveryFeeDO = deliveryFeeRepository.queryTcDeliveryFee(req.getId());
        return deliveryFeeConverter.toDeliveryFeeDTO(deliveryFeeDO);
    }

    /**
     * 查询下单时候 收卖家的取费
     * @param req
     * @return DeliveryFeeDTO
     */
    @Override
    public Long deliveryMerchantFee(DeliveryFeeQueryRpcReq req) {
        TcDeliveryFeeDO deliveryFeeDO = deliveryFeeConverter.toTcDeliveryFeeDO(req);
        // 只查询生效的
        deliveryFeeDO.setActive(CommonConstant.MERCHANT_FEE_ACTIVE);
        deliveryFeeDO.setIsCategoryAll(MerchantDeliveryFeeEnum.ALL.getCode());
        deliveryFeeDO.setIsMerchantAll(MerchantDeliveryFeeEnum.ALL.getCode());
        // 先按照查询条件查询
        List<TcDeliveryFeeDO> deliveryFeeDOS = deliveryFeeRepository.queryDeliveryList(deliveryFeeDO);
        // 没查到 忽略
        if (CollectionUtils.isEmpty(deliveryFeeDOS)) {
           return 0L;
        }
        // 直接匹配
        DeliveryFeeDTO deliveryFeeDTO = deliveryFeeConverter.toDeliveryFeeDTO(
            deliveryFeeDOS.stream()
            .filter(delivery -> Objects.nonNull(delivery))
            .filter(delivery ->
                delivery.getMerchantCode().equals(req.getMerchantCode()) &&
                delivery.getCategoryId().equals(req.getCategoryId()) &&
                delivery.getDeliveryType().equals(req.getDeliveryType()) &&
                delivery.getDeliveryRoute().equals(req.getDeliveryRoute()))
            .findFirst()
            .orElse(null));
        if (Objects.nonNull(deliveryFeeDTO)) {
            return deliveryFeeDTO.getFee();
        }
        // 分类匹配
        deliveryFeeDTO = deliveryFeeConverter.toDeliveryFeeDTO(
            deliveryFeeDOS.stream()
                .filter(delivery -> Objects.nonNull(delivery))
                .filter(delivery ->
                    delivery.getMerchantCode().equals(req.getMerchantCode()) &&
                    delivery.getIsCategoryAll().equals(MerchantDeliveryFeeEnum.ALL.getCode()) &&
                    delivery.getDeliveryType().equals(req.getDeliveryType()) &&
                    delivery.getDeliveryRoute().equals(req.getDeliveryRoute()))
                .findFirst()
                .orElse(null));
        if (Objects.nonNull(deliveryFeeDTO)) {
            return  deliveryFeeDTO.getFee();
        }
        // 卖家匹配
         deliveryFeeDTO = deliveryFeeConverter.toDeliveryFeeDTO(
            deliveryFeeDOS.stream()
            .filter(delivery -> Objects.nonNull(delivery))
            .filter(delivery ->
                delivery.getIsMerchantAll().equals(MerchantDeliveryFeeEnum.ALL.getCode()) &&
                delivery.getCategoryId().equals(req.getCategoryId()) &&
                delivery.getDeliveryType().equals(req.getDeliveryType()) &&
                delivery.getDeliveryRoute().equals(req.getDeliveryRoute())
            )
            .findFirst()
            .orElse(null));
        if (Objects.nonNull(deliveryFeeDTO)) {
            return deliveryFeeDTO.getFee();
        }
        // ALL
        deliveryFeeDTO = deliveryFeeConverter.toDeliveryFeeDTO(
            deliveryFeeDOS.stream()
            .filter(delivery -> Objects.nonNull(delivery))
            .filter(delivery ->
                delivery.getIsMerchantAll().equals(MerchantDeliveryFeeEnum.ALL.getCode()) &&
                delivery.getIsCategoryAll().equals(MerchantDeliveryFeeEnum.ALL.getCode()) &&
                delivery.getDeliveryType().equals(req.getDeliveryType()) &&
                delivery.getDeliveryRoute().equals(req.getDeliveryRoute()))
            .findFirst()
            .orElse(null));
        if (Objects.nonNull(deliveryFeeDTO)) {
            return deliveryFeeDTO.getFee();
        }
        return 0L;
    }
}
