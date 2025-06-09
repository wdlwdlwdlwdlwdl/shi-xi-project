package com.aliyun.gts.gmall.manager.front.trade.adaptor;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.config.I18NConfig;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeRequestConvertor;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.AddEvaluationRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderPickCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.CartItemVO;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemBatchQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.sku.BatchSkuQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.ItemDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuPropDTO;
import com.aliyun.gts.gmall.platform.item.api.facade.item.ItemReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics.LogisticsDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TcOrderOperateFlowRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.UserPickLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRpcReqList;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.LogisticsDetailQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TcOrderOperateFlowDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.TcLogisticsReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.TcOrderOperateFlowReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.UserPickLogisticsWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.evaluation.EvaluationWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.ItemStatusEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.input.*;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerAddressDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.ShopConfigDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerAddressReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerReadFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.testng.collections.Lists;
import org.testng.collections.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.CUSTOMER_CENTER_ERROR;
import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.TRADE_CENTER_ERROR;
import static com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode.*;

/**
 * 交易的其他接口
 *
 * @author tiansong
 */
@Service
@Slf4j
public class TradeAdapter {
    @Autowired
    private EvaluationWriteFacade evaluationWriteFacade;
    @Autowired
    private TcLogisticsReadFacade tcLogisticsReadFacade;
    @Autowired
    private TcOrderOperateFlowReadFacade orderOperateFlowReadFacade;
    @Autowired
    private TradeRequestConvertor tradeRequestConvertor;
    @Autowired
    private CustomerReadFacade customerReadFacade;
    @Autowired
    private CustomerAddressReadFacade customerAddressReadFacade;
    @Autowired
    private ItemReadFacade itemReadFacade;
    @Autowired
    private SellerReadFacade sellerReadFacade;
    @Autowired
    private DatasourceConfig datasourceConfig;
    @Autowired
    private I18NConfig i18NConfig;
    @Autowired
    private UserPickLogisticsWriteFacade userPickLogisticsWriteFacade;

    DubboBuilder tradeBuilder = DubboBuilder.builder().logger(log).sysCode(TRADE_CENTER_ERROR).build();

    DubboBuilder itemBuilder = DubboBuilder.builder().logger(log).strong(Boolean.FALSE).build();

    DubboBuilder userBuilder = DubboBuilder.builder().logger(log).sysCode(CUSTOMER_CENTER_ERROR).build();

    /**
     * 订单评价
     *
     * @param addEvaluationRestCommand
     * @return
     */
    public Boolean addEvaluation(AddEvaluationRestCommand addEvaluationRestCommand) {
        return tradeBuilder.create(datasourceConfig)
            .id(DsIdConst.trade_evaluation_add)
            .queryFunc(
                (Function<AddEvaluationRestCommand, RpcResponse<Boolean>>) request -> {
                EvaluationRpcReqList evaluationRpcReq = new EvaluationRpcReqList();
                List<EvaluationRpcReq> orderEvaluationList = tradeRequestConvertor.convertSubOrderEvaluation(request.getSubOrderEvaluationList());
                evaluationRpcReq.setReqList(orderEvaluationList);
                RpcResponse rpcResponse;
                if (addEvaluationRestCommand.isAdditional()) {
                    // 追评
                    rpcResponse = evaluationWriteFacade.additionalEvaluateOrder(evaluationRpcReq);
                } else {
                    // 普通评价
                    // 添加对主订单的评价信息
                    orderEvaluationList.add(tradeRequestConvertor.convertMainOrderEvaluation(request));
                    rpcResponse = evaluationWriteFacade.evaluateOrder(evaluationRpcReq);
                }
                return rpcResponse.isSuccess() ? RpcResponse.ok(Boolean.TRUE) : RpcResponse.fail(rpcResponse.getFail());
                })
            .bizCode(TRADE_EVALUATION_ERROR)
            .query(addEvaluationRestCommand);
    }

    /**
     * 查询主订单对应的物流信息
     *
     * @param custId
     * @param primaryOrderId
     * @return
     */
    public List<LogisticsDetailDTO> queryLogisticsList(Long custId, Long primaryOrderId) {
        LogisticsDetailQueryRpcReq logisticsDetailQueryRpcReq = new LogisticsDetailQueryRpcReq();
        logisticsDetailQueryRpcReq.setPrimaryOrderId(primaryOrderId);
        logisticsDetailQueryRpcReq.setCustId(custId);
        return tradeBuilder.create()
            .id(DsIdConst.trade_order_queryLogistics)
            .queryFunc((Function<LogisticsDetailQueryRpcReq, RpcResponse<List<LogisticsDetailDTO>>>)
                request -> tcLogisticsReadFacade.queryLogisticsDetail(request)
            )
            .bizCode(LOGISTICS_DETAIL_ERROR)
            .query(logisticsDetailQueryRpcReq);
    }

    public List<CartItemVO> queryCartItemByIds(Set<Long> itemIds) {
        final String lang = LocaleContextHolder.getLocale().getLanguage();
        final String fallback = i18NConfig.getDefaultLang();
        List<ItemDTO> itemDTOList = this.queryItemDTOByIds(itemIds);
        if (CollectionUtils.isEmpty(itemDTOList)) {
            return Collections.EMPTY_LIST;
        }
        // convert
        List<CartItemVO> cartItemVOList = Lists.newArrayList(itemDTOList.size());
        itemDTOList.forEach(itemDTO -> {
            //检测当前语种是否开启
            CartItemVO cartItemVO = new CartItemVO();
            cartItemVO.setItemId(itemDTO.getId());
            if (itemDTO.getTitle() != null) {
                cartItemVO.setItemTitle(itemDTO.getTitle().getValueByLang(lang, fallback));
            }
            cartItemVO.setSelectEnable(ItemStatusEnum.ENABLE.getCode().equals(itemDTO.getStatus()));
            cartItemVOList.add(cartItemVO);
        });
        return cartItemVOList;
    }

    /**
     * 商品信息查询
     * @param itemIds
     * @return
     */
    public List<ItemDTO> queryItemDTOByIds(Set<Long> itemIds) {
        // 商品批量查询
        ItemBatchQueryReq itemBatchQueryReq = new ItemBatchQueryReq();
        itemIds.forEach(itemId -> itemBatchQueryReq.add(ItemQueryReq.create(itemId).withExtend(true).withSku(true)));
        return itemBuilder.create(datasourceConfig)
            .id(DsIdConst.trade_item_queryBatch)
            .queryFunc((Function<ItemBatchQueryReq, RpcResponse<List<ItemDTO>>>) request -> {
                // 查询商品缓存接口
                RpcResponse<List<ItemDTO>> rpcResponse = itemReadFacade.queryCacheItems(request);
                    if (!rpcResponse.isSuccess()) {
                        return RpcResponse.fail(rpcResponse.getFail());
                    }
                    if (CollectionUtils.isEmpty(rpcResponse.getData())) {
                        return RpcResponse.ok(Collections.EMPTY_LIST);
                    }
                    // 过滤在线状态商品
                    List<ItemDTO> itemDTOList = rpcResponse.getData()
                        .stream()
                        .filter(itemDTO -> ItemStatusEnum.ENABLE.getCode().equals(itemDTO.getStatus()))
                        .collect(Collectors.toList());
                    return RpcResponse.ok(itemDTOList);
                })
            .bizCode(TRADE_ITEM_BATCH_ERROR)
            .query(itemBatchQueryReq);
    }

    /**
     *  //key值是skuId_sellerId
     *  Map<String, List<Integer>> loanListMap = new HashMap<>();
     *  //key值是skuId_sellerId
     *  Map<String, List<Integer>> installmentMap = new HashMap<>();
     * @param skuIds
     * @return
     */
    public List<CartItemVO> querySkuByIds(Set<Long> skuIds, Map<String, List<Integer>> loanListMap, Map<String, List<Integer>> installmentMap) {
        BatchSkuQueryReq batchSkuQueryReq = new BatchSkuQueryReq();
        batchSkuQueryReq.add(skuIds);
        return itemBuilder.create(datasourceConfig)
            .id(DsIdConst.trade_sku_queryBatch)
            .queryFunc((Function<BatchSkuQueryReq, RpcResponse<List<CartItemVO>>>) request -> {
                request.setWithSeller(true);
                RpcResponse<List<SkuDTO>> rpcResponse =  null; //;itemReadFacade.queryCacheSkus(request);
                if (!rpcResponse.isSuccess()) {
                    return RpcResponse.fail(rpcResponse.getFail());
                }
                if (CollectionUtils.isEmpty(rpcResponse.getData())) {
                    return RpcResponse.ok(Collections.EMPTY_LIST);
                }
                // convert
                List<CartItemVO> cartItemVOList = Lists.newArrayList(rpcResponse.getData().size());
                rpcResponse.getData().forEach(skuDTO -> {
                    CartItemVO cartItemVO = new CartItemVO();
                    cartItemVO.setItemId(skuDTO.getItemId());
                    cartItemVO.setSkuId(skuDTO.getId());
                    // sku prop 列表已经按照优先级返回list，获取第一个有图片的即可
                    SkuPropDTO skuPropDTO = skuDTO.getSkuPropList()
                        .stream()
                        .filter(skuProp -> StringUtils.isNotBlank(skuProp.getPicUrl()))
                        .findFirst()
                        .orElse(null);
                    cartItemVO.setItemPic(skuPropDTO == null ? null : skuPropDTO.getPicUrl());
                    cartItemVOList.add(cartItemVO);
                });
                return RpcResponse.ok(cartItemVOList);
            })
            .bizCode(TRADE_SKU_BATCH_ERROR)
            .query(batchSkuQueryReq);
    }

    /**
     * 获取用户的默认收货地址
     *
     * @param custId
     * @return
     */
    public CustomerAddressDTO queryDefaultAddress(Long custId) {
        CustomerAddressQuery customerAddressQuery = new CustomerAddressQuery();
        customerAddressQuery.setCustId(custId);
        customerAddressQuery.setDefaultYn(Boolean.TRUE);
        return userBuilder.create(datasourceConfig)
            .id(DsIdConst.trade_customer_address_default).queryFunc((Function<CustomerAddressQuery, RpcResponse<CustomerAddressDTO>>) request -> {
                RpcResponse<PageInfo<CustomerAddressDTO>> rpcResponse = customerAddressReadFacade.pageQuery(request);
                if (!rpcResponse.isSuccess()) {
                    return RpcResponse.fail(rpcResponse.getFail());
                }
                return rpcResponse.getData().isEmpty() ? RpcResponse.OK_VOID :
                    RpcResponse.ok(rpcResponse.getData().getList().get(0));
            })
            .strong(Boolean.FALSE)
            .query(customerAddressQuery);
    }

    /**
     * 获取用户基本信息
     *
     * @param custId
     * @return
     */
    public CustomerDTO queryCustomerById(Long custId) {
        return this.queryCustomer(custId, Boolean.FALSE);
    }

    private CustomerDTO queryCustomer(Long custId, Boolean withAddress) {
        CustomerByIdQuery customerByIdQuery = CustomerByIdQuery.
            of(custId, CustomerQueryOption.builder().needDefaultAddress(withAddress).build());
        return userBuilder.create(datasourceConfig)
            .id(DsIdConst.trade_customer_query)
            .queryFunc(
                (Function<CustomerByIdQuery, RpcResponse<CustomerDTO>>) request -> customerReadFacade.query(request))
            .strong(Boolean.FALSE)
            .query(customerByIdQuery);
    }

    /**
     * 根据ID获取收货地址
     *
     * @param custId
     * @param addressId
     * @return
     */
    public CustomerAddressDTO queryAddressById(Long custId, Long addressId) {
        CommonByIdAndCustByIdQuery commonByIdAndCustByIdQuery = new CommonByIdAndCustByIdQuery();
        commonByIdAndCustByIdQuery.setId(addressId);
        commonByIdAndCustByIdQuery.setCustId(custId);
        return userBuilder.create(datasourceConfig)
            .id(DsIdConst.trade_customer_address_queryById)
            .queryFunc(
                (Function<CommonByIdAndCustByIdQuery, RpcResponse<CustomerAddressDTO>>) request ->
                    customerAddressReadFacade.query(request)
            )
            .bizCode(TRADE_ADDRESS_ERROR)
            .query(commonByIdAndCustByIdQuery);
    }

    public Map<Long/*sellerId*/, SellerDTO> querySellerByIds(Set<Long> sellerIdSet) {
        Map<Long/*sellerId*/, SellerDTO> resultMap = Maps.newConcurrentMap();
        sellerIdSet.parallelStream().forEach(sellerId -> {
            SellerDTO sellerDTO = userBuilder.create(datasourceConfig)
                .id(DsIdConst.trade_seller_queryBatch)
                .queryFunc((Function<CommonByIdQuery, RpcResponse<SellerDTO>>)
                    request -> sellerReadFacade.query(request)
                )
                .strong(Boolean.FALSE)
                .query(CommonByIdQuery.of(sellerId));
            if (sellerDTO != null) {
                resultMap.put(sellerId, sellerDTO);
            }
        });
        return resultMap;
    }

    public ShopConfigDTO queryShopById(Long sellerId) {
        ShopConfigQuery shopConfigQuery = ShopConfigQuery.builder().sellerId(sellerId).build();
        return userBuilder.create(datasourceConfig)
            .id(DsIdConst.trade_seller_queryShop)
            .queryFunc((Function<ShopConfigQuery, RpcResponse<ShopConfigDTO>>)
                request -> sellerReadFacade.queryShop(request)
            )
            .strong(Boolean.FALSE)
            .query(shopConfigQuery);
    }

    /**
     * 查询流水
     * @param primaryOrderId
     * @return
     */
    public List<TcOrderOperateFlowDTO> queryFlowList(Long primaryOrderId) {
        TcOrderOperateFlowRpcReq orderOperateFlowRpcReq = new TcOrderOperateFlowRpcReq();
        orderOperateFlowRpcReq.setPrimaryOrderId(primaryOrderId);
        return tradeBuilder.create()
            .id(DsIdConst.trade_order_getList)
            .queryFunc((Function<TcOrderOperateFlowRpcReq, RpcResponse<List<TcOrderOperateFlowDTO>>>)
                request -> orderOperateFlowReadFacade.queryList(request)
            )
            .bizCode(ORDER_LIST_ERROR)
            .query(orderOperateFlowRpcReq);
    }

    /**
     * 查询流水
     * @param orderPickCommand
     * @return
     */
    public Boolean saveUserLogisticsPick(OrderPickCommand orderPickCommand) {
        UserPickLogisticsRpcReq userPickLogisticsRpcReq = new UserPickLogisticsRpcReq();
        userPickLogisticsRpcReq.setCustId(orderPickCommand.getCustId());
        userPickLogisticsRpcReq.setDeliveryType(orderPickCommand.getDeliveryType());
        return tradeBuilder.create()
            .id(DsIdConst.trade_user_pick)
            .queryFunc((Function<UserPickLogisticsRpcReq, RpcResponse<Boolean>>)
                request -> userPickLogisticsWriteFacade.saveUserPickLogistics(request)
            )
            .bizCode(ORDER_LIST_ERROR)
            .query(userPickLogisticsRpcReq);
    }

}