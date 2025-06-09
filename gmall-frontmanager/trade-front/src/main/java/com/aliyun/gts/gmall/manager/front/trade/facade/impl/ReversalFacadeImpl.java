package com.aliyun.gts.gmall.manager.front.trade.facade.impl;

import com.aliyun.gts.gmall.center.misc.api.utils.ConvertUtils;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.server.util.PublicFileHttpUrl;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.OrderAdapter;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.ReversalAdapter;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.TradeAdapter;
import com.aliyun.gts.gmall.manager.front.trade.component.OrderExtendVOBuildCompContext;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeRequestConvertor;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeResponseConvertor;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.CreateReversalRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.ModifyReversalRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.ReversalBuyerConfirmRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.ReversalDeliverRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PrimaryOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ReversalCheckRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ReversalDetailRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ReversalRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.item.CombineItemVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.OrderMainVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.OrderSubVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.OrderExtendContainerVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.trade.facade.ReversalFacade;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.*;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.util.NumUtils;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.ShopConfigDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testng.collections.Lists;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 售后服务实现
 *
 * @author tiansong
 */
@Service
public class ReversalFacadeImpl implements ReversalFacade {
    @Autowired
    private OrderAdapter           orderAdapter;
    @Autowired
    private ReversalAdapter        reversalAdapter;
    @Autowired
    private TradeAdapter           tradeAdapter;
    @Autowired
    private TradeRequestConvertor  tradeRequestConvertor;
    @Autowired
    private TradeResponseConvertor tradeResponseConvertor;

    @Autowired
    OrderExtendVOBuildCompContext orderExtendVOBuildCompContext;

    @Autowired
    private PublicFileHttpUrl publicFileHttpUrl;

    @Value("${front-manager.default-icon}")
    private String defaultIcon;

    @Override
    public ReversalOrderDetailVO queryForCreate(ReversalCheckRestQuery reversalCheckRestQuery) {
        // 1. 订单信息
        PrimaryOrderRestQuery primaryOrderRestQuery = tradeRequestConvertor.convertOrderQuery(reversalCheckRestQuery);
        MainOrderDetailDTO orderDetailDTO = orderAdapter.getDetail(primaryOrderRestQuery);
        if (orderDetailDTO == null) {
            throw new FrontManagerException(TradeFrontResponseCode.ORDER_DETAIL_ERROR);
        }
        // 2. 售后原因
        List<ReversalReasonDTO> reasonList = reversalAdapter.queryReasonList(
            ReversalTypeEnum.codeOf(reversalCheckRestQuery.getReversalType()));
        if (CollectionUtils.isEmpty(reasonList)) {
            throw new FrontManagerException(TradeFrontResponseCode.REVERSAL_REASON_ERROR);
        }
        // 3. 可申请售后数量
        ReversalCheckDTO reversalCheckDTO = reversalAdapter.checkOrder(reversalCheckRestQuery);
        List<ReversalSubOrderDTO> reversalSubOrderDTOList = reversalCheckDTO.getSubOrders();
        if (CollectionUtils.isEmpty(reversalSubOrderDTOList)) {
            throw new FrontManagerException(TradeFrontResponseCode.REVERSAL_CHECK_ERROR);
        }
        // 4. 结果组装
        OrderExtendContainerVO orderExtendContainerVO = orderExtendVOBuildCompContext.buildExtend(orderDetailDTO);
        OrderMainVO orderMainVO = tradeResponseConvertor.convertOrderDetail(orderDetailDTO,orderExtendContainerVO);
        List<ReversalSubOrderVO> sublist = ConvertUtils.converts( reversalSubOrderDTOList,tradeResponseConvertor::convertReversalSubOrder);
        ReversalOrderDetailVO reversalOrderDetailVO = new ReversalOrderDetailVO();
        reversalOrderDetailVO.setReasonDTOList(reasonList);
        reversalOrderDetailVO.setOrderMainVO(orderMainVO);
        reversalOrderDetailVO.setReversalSubOrderDTOS(sublist);
        //拼接组合商品信息
        this.fillCombineItemReversal(orderMainVO,reversalSubOrderDTOList);
        // 根据子订单ID，过滤掉其他子订单信息
        this.fillSubOrderList(reversalCheckRestQuery.getSubOrderId(), reversalOrderDetailVO, reversalCheckDTO);
        // 校验过滤后的子订单是否可以申请退款
        ReversalSubOrderVO notAllowReversal = reversalOrderDetailVO.getReversalSubOrderDTOS().stream()
                .filter(v->!v.isAllowReversal()).findFirst().orElse(null);
        if (notAllowReversal != null) {
            throw new FrontManagerException(TradeFrontResponseCode.REVERSAL_CHECK_ERROR);
        }
        return reversalOrderDetailVO;
    }
    private void fillCombineItemReversal(OrderMainVO orderMainVO,List<ReversalSubOrderDTO> checkReversal){
        Map<Long,ReversalSubOrderDTO> maps = new HashMap<>();
        if(checkReversal != null) {
            maps = checkReversal.stream().collect(Collectors.toMap(m -> m.getOrderId(), m -> m));
        }
        for (OrderSubVO subVO : orderMainVO.getSubOrderList()) {
            ReversalSubOrderDTO dto = maps.get(subVO.getOrderId());
            if (dto == null) {
                continue;
            }
            List<CombineItemVO> vo = tradeResponseConvertor.reversalCombineItem(dto);
            subVO.setCombineItems(vo);
        }
    }

    /**
     * 按照子订单ID进行过滤，只剩余该子订单信息
     */
    private void fillSubOrderList(Long subOrderId, ReversalOrderDetailVO reversalOrderDetailVO, ReversalCheckDTO reversalCheckDTO) {
        if (subOrderId == null) {
            return;
        }
        List<ReversalSubOrderVO> reversalSubOrderDTOList = reversalOrderDetailVO.getReversalSubOrderDTOS();
        if (CollectionUtils.isNotEmpty(reversalSubOrderDTOList)) {
            List<ReversalSubOrderVO> list = reversalSubOrderDTOList.stream().filter(reversalSubOrderDTO ->
                    subOrderId.equals(reversalSubOrderDTO.getOrderId())).collect(Collectors.toList());
            reversalOrderDetailVO.setReversalSubOrderDTOS(list);

            // 前台运费与子单一起退, 输入一个含运费金额
            long freight = NumUtils.getNullZero(reversalCheckDTO.getMaxCancelFreightAmt());
            if (freight > 0 && !list.isEmpty()) {
                ReversalSubOrderVO head = list.get(0);
                head.setMaxFreightAmt(freight);
                head.setMaxCancelAmt(NumUtils.getNullZero(head.getMaxCancelAmt()) + freight);
            }
        }
        OrderMainVO orderMainVO = reversalOrderDetailVO.getOrderMainVO();
        if (orderMainVO != null && CollectionUtils.isNotEmpty(orderMainVO.getSubOrderList())) {
            orderMainVO.setSubOrderList(orderMainVO.getSubOrderList().stream().filter(orderSubVO ->
                subOrderId.equals(orderSubVO.getOrderId())).collect(Collectors.toList()));
        }
    }

    @Override
    public Long create(CreateReversalRestCommand createReversalRestCommand) {
        // http to oss
        if (CollectionUtils.isNotEmpty(createReversalRestCommand.getCustMedias())) {
            createReversalRestCommand.setCustMedias(
                createReversalRestCommand
                    .getCustMedias()
                    .stream()
                    .map(value -> publicFileHttpUrl.httpToOssOrMinio(value))
                    .collect(Collectors.toList()));
        }
        return reversalAdapter.create(createReversalRestCommand);
    }

    @Override
    public Boolean cancel(ModifyReversalRestCommand modifyReversalRestCommand) {
        reversalAdapter.cancel(modifyReversalRestCommand);
        return Boolean.TRUE;
    }

    @Override
    public ReversalDetailVO queryDetail(ReversalDetailRestQuery reversalDetailRestQuery) {
        MainReversalDTO mainReversalDTO = reversalAdapter.queryDetail(reversalDetailRestQuery.getPrimaryReversalId());
        // 订单校验是否存在，或归属于当前用户
        if (mainReversalDTO == null || !reversalDetailRestQuery.getCustId().equals(mainReversalDTO.getCustId())) {
            throw new FrontManagerException(TradeFrontResponseCode.REVERSAL_DETAIL_ERROR);
        }
        ReversalDetailVO reversalDetailVO = tradeResponseConvertor.convertReversalDetail(mainReversalDTO);
        // 填充主订单信息
        this.fillMainOrder(reversalDetailRestQuery, reversalDetailVO);
        // 填充协商历史
        this.fillHistory(reversalDetailVO, mainReversalDTO.getFlows());
        return reversalDetailVO;
    }

    private void fillHistory(ReversalDetailVO reversalDetailVO, List<ReversalFlowDTO> reversalFlowDTOList) {
        CustomerDTO customerDTO = tradeAdapter.queryCustomerById(reversalDetailVO.getCustId());
        String custLogo = customerDTO == null || StringUtils.isBlank(customerDTO.getHeadUrl()) ?
                defaultIcon : customerDTO.getHeadUrl();

        ShopConfigDTO shopConfigDTO = tradeAdapter.queryShopById(reversalDetailVO.getSellerId());
        String sellerLogo = shopConfigDTO == null || StringUtils.isBlank(shopConfigDTO.getLogoUrl()) ?
                defaultIcon : shopConfigDTO.getLogoUrl();
        reversalDetailVO.setShowHistory(Boolean.TRUE);
        List<ReversalHistoryVO> reversalHistoryVOList = Lists.newArrayList();
        // 申请记录
        ReversalHistoryVO reversalHistoryVO = ReversalHistoryVO.builder().eventTime(reversalDetailVO.getGmtCreate())
                .custLogo(custLogo).custNick(reversalDetailVO.getCustName()).content(reversalDetailVO.getCustMemo())
                .urlList(this.ossToHttp(reversalDetailVO.getCustMedias())).build();
        reversalHistoryVOList.add(reversalHistoryVO);
        // 其他操作记录
        if (CollectionUtils.isNotEmpty(reversalFlowDTOList)) {
            String sellerMemo = reversalDetailVO.getSellerMemo();
            List<String> sellerMedias = reversalDetailVO.getSellerMedias();
            reversalFlowDTOList.forEach(reversalFlowDTO -> {
                Boolean isSeller = new Integer(0).equals(reversalFlowDTO.getCustOrSeller());
                ReversalHistoryVO reversalHistory = ReversalHistoryVO.builder().eventTime(reversalFlowDTO.getGmtCreate())
                        .custLogo(isSeller ? sellerLogo : custLogo)
                        // 卖家拒绝理由: 文案
                        .content((StringUtils.isBlank(sellerMemo) || !isSellerRefuse(reversalFlowDTO)) ?
                                reversalFlowDTO.getOpName() : reversalFlowDTO.getOpName() + ": " + sellerMemo)
                        .custNick(isSeller ? reversalDetailVO.getSellerName() : reversalDetailVO.getCustName())
                        // 卖家拒绝理由: 图片URL
                        .urlList((CollectionUtils.isEmpty(sellerMedias) || !isSellerRefuse(reversalFlowDTO)) ?
                                null : this.ossToHttp(sellerMedias)).build();
                reversalHistoryVOList.add(reversalHistory);
            });
            // 重新排序
            Collections.reverse(reversalHistoryVOList);
        }
        reversalDetailVO.setHistoryList(reversalHistoryVOList);
    }

    private boolean isSellerRefuse(ReversalFlowDTO reversalFlowDTO) {
        return ReversalStatusEnum.SELLER_REFUSE.getCode().equals(reversalFlowDTO.getToReversalStatus());
    }

    private List<String> ossToHttp(List<String> urlList) {
        if (CollectionUtils.isEmpty(urlList)) {
            return null;
        }
        return urlList.stream().map(url ->
                publicFileHttpUrl.getFileHttpUrl(url, true)).collect(Collectors.toList());
    }

    private void fillMainOrder(ReversalDetailRestQuery reversalDetailRestQuery, ReversalDetailVO reversalDetailVO) {
        PrimaryOrderRestQuery primaryOrderRestQuery = new PrimaryOrderRestQuery();
        primaryOrderRestQuery.setCustId(reversalDetailRestQuery.getCustId());
        primaryOrderRestQuery.setChannel(reversalDetailRestQuery.getChannel());
        primaryOrderRestQuery.setPrimaryOrderId(reversalDetailVO.getPrimaryOrderId());
        primaryOrderRestQuery.checkInput();
        MainOrderDetailDTO mainOrderDetailDTO = orderAdapter.getDetail(primaryOrderRestQuery);
        OrderExtendContainerVO orderExtendContainerVO = orderExtendVOBuildCompContext.buildExtend(mainOrderDetailDTO);
        reversalDetailVO.setOrderInfo(tradeResponseConvertor.convertOrderDetail(mainOrderDetailDTO,orderExtendContainerVO));
    }

    @Override
    public PageInfo<ReversalOrderVO> queryList(ReversalRestQuery reversalRestQuery) {
        PageInfo<MainReversalDTO> pageInfo = reversalAdapter.queryList(reversalRestQuery);
        if (pageInfo == null || pageInfo.isEmpty()) {
            return PageInfo.empty();
        }
        List<ReversalOrderVO> reversalOrderVOList = tradeResponseConvertor.convertReversalList(pageInfo.getList());
        // fill seller name;最好是搜索引擎中增加sellerName字段
        Map<Long, SellerDTO> sellerDTOMap = tradeAdapter.querySellerByIds(
            reversalOrderVOList.stream().map(ReversalOrderVO::getSellerId).collect(Collectors.toSet()));
        reversalOrderVOList.forEach(reversalOrderVO -> {
            SellerDTO sellerDTO = sellerDTOMap.get(reversalOrderVO.getSellerId());
            reversalOrderVO.setSellerName(sellerDTO == null ? BizConst.SELLER_NICK : sellerDTO.getNickname());
        });
        return new PageInfo(pageInfo.getTotal(), reversalOrderVOList);
    }

    @Override
    public Boolean sendDeliver(ReversalDeliverRestCommand reversalDeliverRestCommand) {
        reversalAdapter.sendDeliver(reversalDeliverRestCommand);
        return Boolean.TRUE;
    }

    @Override
    public Boolean buyerConfirmRefund(ReversalBuyerConfirmRestCommand command) {
        reversalAdapter.buyerConfirmRefund(command);
        return true;
    }
}