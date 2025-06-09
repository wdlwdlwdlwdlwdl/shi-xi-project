package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.processengine.WorkflowEngine;
import com.aliyun.gts.gmall.middleware.oss.GmallOssClient;
import com.aliyun.gts.gmall.platform.promotion.common.util.JsonUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtendQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.extend.OrderExtendDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.*;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.config.WorkflowProperties;
import com.aliyun.gts.gmall.platform.trade.core.convertor.SearchOrderConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderExtraService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderSearchService;
import com.aliyun.gts.gmall.platform.trade.core.util.RpcServerUtils;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderReadFacadeImpl implements OrderReadFacade {

    @Value("${search.type:opensearch}")
    private String searchType;

    @Autowired
    private WorkflowEngine workflowEngine;

    @Autowired
    OrderSearchService orderSearchService;

    @Autowired
    private WorkflowProperties workflowProperties;

    @Autowired
    private OrderExtraService orderExtraService;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private SearchOrderConverter searchOrderConverter;

    @Autowired
    private GmallOssClient publicGmallOssClient;

    /**
     * 订单确认接口
     * @param req
     * @return
     */
    @Override
    public RpcResponse<ConfirmOrderDTO> confirmOrderInfo(ConfirmOrderInfoRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TOrderConfirm ord = new TOrderConfirm();
            ord.setReq(req);
            ord.setDomain(new CreatingOrder());
            workflowEngine.invokeAndGetResult(workflowProperties.getOrderConfirm(), ord.toWorkflowContext());
            return ord.getResponse();
        }, "OrderReadFacadeImpl.confirmOrderInfo", BizCodeEntity.buildByReq(req));
    }

    /**
     * 订单确认展示  新版本
     *    仅用来计算订单商品信息不生成订单
     * @anthor shifeng
     * @param confirmOrderInfoRpcReq
     * @return
     */
    @ApiOperation("订单确认展示")
    @Override
    public RpcResponse<ConfirmOrderDTO> confirmOrderInfoNew(ConfirmOrderInfoRpcReq confirmOrderInfoRpcReq) {
        return RpcServerUtils.invoke(() -> {
            TOrderConfirm ord = new TOrderConfirm();
            ord.setReq(confirmOrderInfoRpcReq);
            ord.setDomain(new CreatingOrder());
            workflowEngine.invokeAndGetResult(workflowProperties.getOrderConfirmNew(), ord.toWorkflowContext());
            return ord.getResponse();
        }, "OrderReadFacadeImpl.confirmOrderInfo", BizCodeEntity.buildByReq(confirmOrderInfoRpcReq));
    }


    @Override
    public RpcResponse<PageInfo<MainOrderDTO>> queryCustOrderList(CustomerOrderQueryRpcReq req) {
        BizCodeEntity bizCodeEntity = BizCodeEntity.buildByReq(req);
        Map<String, Object> searchContext = new HashMap<>();
        searchContext.put("customerOrderQueryRpcReq", req);
        String flowPath = workflowProperties.getOrderCustomerQuery();
        if (!isDbQuery(req)) {
            flowPath = CommonConstant.OPENSEARCH.equals(searchType) ?
                workflowProperties.getOrderCustomerSearch() :
                workflowProperties.getOrderCustomerEsSearch();
        }
        final String path = flowPath;
        return RpcServerUtils.invoke(() -> {
            FlowNodeResult searchResult = (FlowNodeResult) workflowEngine.invokeAndGetResult(path, searchContext);
            if (!searchResult.isSuccess()) {
                log.error("OrderReadFacadeImpl.queryBuyerOrderList return occurred exceptions!");
                return RpcResponse.fail(searchResult.getFail());
            }
            return RpcResponse.ok((PageInfo<MainOrderDTO>) searchResult.getNodeData());
        }, "OrderReadFacadeImpl.queryCustOrderList", bizCodeEntity);
    }

    private boolean isDbQuery(CustomerOrderQueryRpcReq req) {
        if (StringUtils.isNotBlank(req.getItemTitle())) {
            return false;
        }
        if (req.getStatusList() != null) {
            for (OrderStatusInfo status : req.getStatusList()) {
                if (status.getStepNo() != null || status.getStepStatus() != null) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public RpcResponse<PageInfo<MainOrderDTO>> querySellerOrderList(SellerOrderQueryRpcReq req) {
        // 指定ID的查询
        if (req.getPrimaryOrderId() != null) {
            OrderDetailQueryRpcReq detailQuery = new OrderDetailQueryRpcReq();
            detailQuery.setPrimaryOrderId(req.getPrimaryOrderId());
            detailQuery.setSeller(true);
            RpcResponse<MainOrderDetailDTO> detailResult = queryOrderDetail(detailQuery);
            if (!detailResult.isSuccess()) {
                if (detailResult.getFail() != null
                        && OrderErrorCode.ORDER_NOT_EXISTS.getCode().equals(detailResult.getFail().getCode())) {
                    // 订单不存在 ignore this errorCode
                } else {
                    return RpcResponse.fail(detailResult.getFail());
                }
            }
            MainOrderDetailDTO data = detailResult.getData();
            if (data != null) {
                if (req.getSellerId() != null && req.getSellerId().longValue() > 0
                        && req.getSellerId().longValue() != data.getSellerId().longValue()) {
                    data = null;
                }
            }
            PageInfo page = new PageInfo();
            if (data == null) {
                page.setTotal(0L);
                page.setList(new ArrayList());
            } else {
                page.setTotal(1L);
                page.setList(List.of(data));
            }
            return RpcResponse.ok(page);
        }

        BizCodeEntity bizCodeEntity = BizCodeEntity.buildByReq(req);
        Map<String, Object> searchContext = new HashMap<>();
        searchContext.put("sellerOrderQueryRpcReq", req);
        log.info("sellerOrderQueryRpcReq={}", JsonUtils.toJSONString(req));

        String workflowPath = CommonConstant.OPENSEARCH.equals(searchType)? workflowProperties.getOrderSellerSearch(): workflowProperties.getOrderSellerEsSearch();

        return RpcServerUtils.invoke(() -> {
            FlowNodeResult searchResult = (FlowNodeResult) workflowEngine.invokeAndGetResult(workflowPath, searchContext);
            if (!searchResult.isSuccess()) {
                log.error("OrderReadFacadeImpl.querySellerOrderList return occurred exceptions!");
                return RpcResponse.fail(searchResult.getFail());
            }
            return RpcResponse.ok((PageInfo<MainOrderDTO>) searchResult.getNodeData());
        }, "OrderReadFacadeImpl.querySellerOrderList", bizCodeEntity);
    }

    @Override
    public RpcResponse<PageInfo<MainOrderDTO>> queryOrderList(OrderSearchRpcReq req) {
        BizCodeEntity bizCodeEntity = BizCodeEntity.buildByReq(req);
        Map<String, Object> searchContext = new HashMap<>();
        searchContext.put("orderSearchRpcReq", req);
        String workflowPath = CommonConstant.OPENSEARCH.equals(searchType) ? workflowProperties.getOrderCommonSearch() : workflowProperties.getOrderCommonEsSearch();
        return RpcServerUtils.invoke(() -> {
            FlowNodeResult searchResult = (FlowNodeResult) workflowEngine.invokeAndGetResult(workflowPath, searchContext);
            if (!searchResult.isSuccess()) {
                log.error("OrderReadFacadeImpl.queryOrderList return occurred exceptions!");
                return RpcResponse.fail(searchResult.getFail());
            }
            return RpcResponse.ok((PageInfo<MainOrderDTO>) searchResult.getNodeData());
        }, "OrderReadFacadeImpl.queryOrderList", bizCodeEntity);
    }

    @Override
    public RpcResponse<MainOrderDetailDTO> queryOrderDetail(OrderDetailQueryRpcReq orderDetailQueryRpcReq) {
        BizCodeEntity bizCodeEntity = BizCodeEntity.buildByReq(orderDetailQueryRpcReq);
        Map<String, Object> context = new HashMap<>();
        context.put("orderDetailQueryRpcReq", orderDetailQueryRpcReq);
        return RpcServerUtils.invoke(() -> {
            FlowNodeResult result = (FlowNodeResult) workflowEngine.invokeAndGetResult(workflowProperties.getOrderDetail(), context);
            return RpcResponse.ok((MainOrderDetailDTO) result.getNodeData());
        }, "OrderReadFacadeImpl.queryOrderDetail", bizCodeEntity);
    }


    @Override
    public RpcResponse<List<MainOrderDetailDTO>> queryOrderDetailNew(OrderDetailQueryRpcReq orderDetailQueryRpcReq) {
        BizCodeEntity bizCodeEntity = BizCodeEntity.buildByReq(orderDetailQueryRpcReq);
        Map<String, Object> context = new HashMap<>();
        context.put("orderDetailQueryRpcReq", orderDetailQueryRpcReq);
        return RpcServerUtils.invoke(() -> {
            FlowNodeResult result = (FlowNodeResult) workflowEngine.invokeAndGetResult(workflowProperties.getOrderDetailNew(), context);
            return RpcResponse.ok((List<MainOrderDetailDTO>) result.getNodeData());
        }, "OrderReadFacadeImpl.queryOrderDetail", bizCodeEntity);
    }


    @Override
    public RpcResponse<Map<Integer, Integer>> countOrderByStatus(CountOrderQueryRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            return RpcResponse.ok(orderSearchService.countByStatus(req));
        }, "OrderReadFacadeImpl.countOrderByStatus");
    }

    @Override
    public RpcResponse<List<OrderExtendDTO>> queryOrderExtend(OrderExtendQueryRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            req.checkInput();
            return RpcResponse.ok(orderExtraService.queryOrderExtend(req));
        }, "OrderReadFacadeImpl.queryOrderExtend");
    }

    @Override
    public RpcResponse<List<MainOrderDTO>> batchQueryOrderInfo(OrderBatchQueryRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            OrderQueryOption option = OrderQueryOption.builder()
                    .includeExtends(req.isIncludeExtend())
                    .includeReversalInfo(req.isIncludeReversalInfo())
                    .build();
            List<MainOrder> list = orderQueryAbility.batchGetMainOrder(req.getPrimaryOrderIds(), option);
            List<MainOrderDTO> convertList = list.stream().map(this::convert).collect(Collectors.toList());
            return RpcResponse.ok(convertList);
        }, "OrderReadFacadeImpl.batchQueryOrderInfo");
    }

    private MainOrderDTO convert(MainOrder mainOrder) {
        MainOrderDTO mainOrderDTO = searchOrderConverter.convertMainOrder(mainOrder);
        List<SubOrder>  subOrders = mainOrder.getSubOrders();
        for(SubOrder subOrder : subOrders){
            SubOrderDTO subDTO = searchOrderConverter.convertSubOrder(subOrder);
            subDTO.setItemPic(publicGmallOssClient.getFileHttpUrl(subDTO.getItemPic()));
            mainOrderDTO.getSubOrderList().add(subDTO);
        }
        return mainOrderDTO;
    }

    @Override
    public RpcResponse<List<OrderStatisticsDTO>> statisticsBySellerIds(OrderStatisticsQueryRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            List<OrderStatisticsDTO> list = orderSearchService.statisticsBySellerIds(req);
            return RpcResponse.ok(list);
        }, "OrderReadFacadeImpl.statisticsBySellerIds");
    }

    @Override
    public RpcResponse<List<OrderStatisticsDTO>> statisticsBySeller(OrderStatisticsQueryRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            List<OrderStatisticsDTO> list = orderSearchService.statisticsBySeller(req);
            return RpcResponse.ok(list);
        }, "OrderReadFacadeImpl.statisticsBySeller");
    }

    @Override
    public RpcResponse<OrderSalesStatisticsDTO> statisticsOrderSales(OrderSalesStatisticsQueryRpcReq req) {
        OrderSalesStatisticsDTO dto = new OrderSalesStatisticsDTO();
        return RpcResponse.ok(dto);
    }

    @Override
    public RpcResponse<List<OrderSummaryStatisticsDTO>> queryOrderSummaryList(OrderSummaryQueryRpcReq req) {
        return null;
    }

    @Override
    public RpcResponse<PageInfo<MainOrderDTO>> queryAdminOrderList(SellerOrderQueryRpcReq req) {
        // 指定ID的查询
        if (req.getPrimaryOrderId() != null) {
            OrderDetailQueryRpcReq detailQuery = new OrderDetailQueryRpcReq();
            detailQuery.setPrimaryOrderId(req.getPrimaryOrderId());
            detailQuery.setSeller(true);
            RpcResponse<MainOrderDetailDTO> detailResult = queryOrderDetail(detailQuery);
            if (!detailResult.isSuccess()) {
                if (detailResult.getFail() != null
                        && OrderErrorCode.ORDER_NOT_EXISTS.getCode().equals(detailResult.getFail().getCode())) {
                    // 订单不存在 ignore this errorCode
                } else {
                    return RpcResponse.fail(detailResult.getFail());
                }
            }
            MainOrderDetailDTO data = detailResult.getData();
            if (data != null) {
                if (req.getSellerId() != null && req.getSellerId().longValue() > 0
                        && req.getSellerId().longValue() != data.getSellerId().longValue()) {
                    data = null;
                }
            }
            PageInfo page = new PageInfo();
            if (data == null) {
                page.setTotal(0L);
                page.setList(new ArrayList());
            } else {
                page.setTotal(1L);
                page.setList(List.of(data));
            }
            return RpcResponse.ok(page);
        }

        BizCodeEntity bizCodeEntity = BizCodeEntity.buildByReq(req);
        Map<String, Object> searchContext = new HashMap<>();
        searchContext.put("sellerOrderQueryRpcReq", req);
        String workflowPath = workflowProperties.getOrderAdminSearch();
        return RpcServerUtils.invoke(() -> {
            FlowNodeResult searchResult = (FlowNodeResult) workflowEngine.invokeAndGetResult(workflowPath, searchContext);
            if (!searchResult.isSuccess()) {
                log.error("OrderReadFacadeImpl.querySellerOrderList return occurred exceptions!");
                return RpcResponse.fail(searchResult.getFail());
            }
            return RpcResponse.ok((PageInfo<MainOrderDTO>) searchResult.getNodeData());
        }, "OrderReadFacadeImpl.querySellerOrderList", bizCodeEntity);
    }
}

