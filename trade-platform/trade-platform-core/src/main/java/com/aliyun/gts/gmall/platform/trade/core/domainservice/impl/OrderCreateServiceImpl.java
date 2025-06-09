package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.DeliveryTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderSaveAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.core.util.SplitOrderUtils;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.*;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CacheRepository;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderCreateServiceImpl implements OrderCreateService {
    private static final String EXT_IDEMPOTENT_KEY = "__gmall_createOrderKey";
    private static final String PRESSURE_TEST_TOKEN = "PRESSURE_ORDER_1c979a83-55ff-47ea-a351-8ae2a201cad1";

    private static final int LOCK_TIME_MINUTE = 30;
    private static final long LOCK_STATUS_1 = 1L;   // 未使用
    private static final long LOCK_STATUS_2 = 2L;   // 锁定中
    private static final long LOCK_STATUS_3 = 3L;   // 已使用

    @Value("${trade.createOrder.useShortToken:true}")
    private Boolean useShortToken;

    @Autowired
    private CacheRepository cacheRepository;

    @Autowired
    private OrderSaveAbility orderSaveAbility;

    @Override
    public List<MainOrder> splitOrder(Collection<ItemSku> itemList) {
        // 按卖家分组
        Multimap<Long, ItemSku> sellerMap = LinkedHashMultimap.create();
        for (ItemSku item : itemList) {
            sellerMap.put(item.getSeller().getSellerId(), item);
        }
        List<MainOrder> result = new ArrayList<>();
        for (Long sellerId : sellerMap.keySet()) {
            MainOrder main = null;
            for (ItemSku item : sellerMap.get(sellerId)) {
                if (main == null) {
                    main = new MainOrder();
                    main.setSeller(item.getSeller());
                    main.setSubOrders(new ArrayList<>());
                    result.add(main);
                }
                SubOrder sub = new SubOrder();
                sub.setItemSku(item);
                sub.setSkuQuoteId(item.getSkuQuoteId());
                sub.orderAttr().setWeight(item.getWeight());
                sub.orderAttr().setCanRefunds(item.getCanRefunds());
                sub.orderAttr().setItemType(item.getItemType());
                main.getSubOrders().add(sub);
//                main.set(String.valueOf(item.getDeliveryType()));
            }
        }

        //三级商品类别允许生成一个订单。（该项配置，在商品类别上配置，可修改），
        for (MainOrder mainOrder : result) {
            if (mainOrder.getSubOrders().size() > 1) {
                //说明可能需要拆分，判断出下面所有不允许的，都拆出来即可，允许的类目都放一起
                SubOrder sub = null;
                for (int i = mainOrder.getSubOrders().size(); i > 0; i--)   {
                    sub = mainOrder.getSubOrders().get(i - 1);
                    if (sub.getItemSku().getItemCategory().getThirdCategorySplitOrder()) {
                        //单独拆出来成一个订单
                        mainOrder.getSubOrders().remove(sub);

                        MainOrder newMain = new MainOrder();
                        newMain.setSubOrders(new ArrayList<>());
                        newMain.setSeller(mainOrder.getSeller());
                        newMain.getSubOrders().add(sub);
                        result.add(newMain);
                        break;
                    }
                }
            }
        }
        //所有的商品都可以从同一个仓库(销售点)买到
        MainOrder mainOrder = null;
        for (int i = result.size(); i > 0; i--) {
             mainOrder = result.get(i - 1);
            if (mainOrder.getSubOrders().size() > 1) {
                //说明可能需要拆分，判断所有的商品都可以从同一个仓库(销售点)买到，采取优先多的放一起的原则
                result.remove(i - 1);
                result.addAll(splitMainOrderByWarehouse(mainOrder));
            }
        }

        return result;
    }

    /**
     * 根据商品进行拆单
     *    1、卖家 --- 不同卖家 不可以合单
     *    2、商品类别 --- 商品类别有参数 是否允许合单 允许合单则可以加入合单 不允许 则单独一个商品一个订单
     *    3、仓库  --- 拆单 最复杂
     *       在完成上面两个拆合单后 ，仅有一个商品 不需要再拆分
     *       按照相同城市优先的原则计算 相同城市先拆出来
     *       不同城市的 按照收货地点合单即可
     * @param orderItems
     * @return  List<MainOrder>
     * 2024-12-13 15:14:58
     */
    @Override
    public List<MainOrder> splitOrderNew(List<SplitOrderItemInfo> orderItems) {
        // step1 按卖家分组 --- 将商品按照卖家分成不同的分组
        Multimap<Long, SplitOrderItemInfo> sellerMap = LinkedHashMultimap.create();
        for (SplitOrderItemInfo splitOrderItemInfo : orderItems) {
            sellerMap.put(splitOrderItemInfo.getSellerId(), splitOrderItemInfo);
        }
        List<List<SplitOrderItemInfo>> splitOrder = new ArrayList<>();
        // step2 遍历每个卖家的商品 取搜分类是否允许合单， 只有允许合单的商品才可以， 不允许的直接单独一单
        for (Long sellerId : sellerMap.keySet()) {
            Collection<SplitOrderItemInfo> splitOrderItemInfos = sellerMap.get(sellerId);
            if (CollectionUtils.isEmpty(splitOrderItemInfos)) {
                continue;
            }
            // 商品分类list
            List<SplitOrderItemInfo> categoryList = new ArrayList<>();
            // 遍历每个商品  获取允许分类的单品集合
            for (SplitOrderItemInfo splitOrderItemInfo : splitOrderItemInfos) {
                // 设置仓库点 所有仓库点
                splitOrderItemInfo.setWarehourseStockList(splitOrderItemInfo.getItemSku().getStockList());
                // 物流方式为空 单独算一单 不考虑拆单
                //if (splitOrderItemInfo.getDeliveryType() == null) {
                //    List<SplitOrderItemInfo> list = new ArrayList<>();
                //    list.add(splitOrderItemInfo);
                //    splitOrder.add(list);
                //    continue;
                //}
                // 收货地址为空的  单独算一单 不考虑拆单
                //if (splitOrderItemInfo.getReceiver() == null ||
                //    splitOrderItemInfo.getReceiver().getReceiverId() == null) {
                //    List<SplitOrderItemInfo> list = new ArrayList<>();
                //    list.add(splitOrderItemInfo);
                //    splitOrder.add(list);
                //    continue;
                //}
                // 不允许合单分组 单独一单
                if (Boolean.FALSE.equals(splitOrderItemInfo.getItemSku().getItemCategory().getThirdCategorySplitOrder())) {
                    List<SplitOrderItemInfo> list = new ArrayList<>();
                    list.add(splitOrderItemInfo);
                    splitOrder.add(list);
                    continue;
                }
                // 允许合单分组放一起
                categoryList.add(splitOrderItemInfo);
            }
            //如果存在分类允许合单的商品，只有一个 忽略 大于1个 要合单计算
            if(CollectionUtils.isEmpty(categoryList)) {
                continue;
            }
            // 只有一个 不用合单 直接单独计算集合
            if(categoryList.size() == 1) {
                List<SplitOrderItemInfo> list = new ArrayList<>();
                list.addAll(categoryList);
                splitOrder.add(list);
                continue;
            }
            /**
             * step 3
             * 从仓库点里面找出有重合的商品， 组合订单 重新分组
             */
            //splitOrder.add(categoryList);
            //Multimap<String, SplitOrderItemInfo> splitMap = LinkedHashMultimap.create();
            //for (SplitOrderItemInfo splitOrderItemInfo : categoryList) {
            //    splitMap.put(String.format("%s_%s", splitOrderItemInfo.getDeliveryType(), splitOrderItemInfo.getReceiver().getReceiverId()), splitOrderItemInfo);
            //}
            // 分组后再重新计算
            //for (String key : splitMap.keySet()) {
            //    Collection<SplitOrderItemInfo> sameOrderCollection = splitMap.get(key);
            //    List<SplitOrderItemInfo> sameOrderList = new ArrayList<>(sameOrderCollection);
            //}
            splitOrder.addAll(SplitOrderUtils.splitOrder(categoryList));
        }
        // 构建主订单
        return converToMainOrder(splitOrder);
    }

    /**
     * 构建主订单数据
     * @param splitOrder
     * @return List<MainOrder>
     */
    private List<MainOrder> converToMainOrder (List<List<SplitOrderItemInfo>> splitOrder) {
        // 返回结果
        List<MainOrder> splitMainOrder = new ArrayList<>();
        // 拆分商品后 按照商品结果生成订单对象数据
        for(List<SplitOrderItemInfo> splitOrderItemInfos : splitOrder) {
            if (CollectionUtils.isEmpty(splitOrderItemInfos)) {
                continue;
            }
            // 主单
            MainOrder mainOrder = new MainOrder();
            // 物流方式
            List<String> supportDeliveryList = DeliveryTypeEnum.initDeliverList();
            for (SplitOrderItemInfo splitOrderItemInfo :splitOrderItemInfos) {
                if (Objects.isNull(splitOrderItemInfo)) {
                    continue;
                }
                // 一定是相同的卖家
                mainOrder.setCartId(splitOrderItemInfo.getCartId());
                mainOrder.setCityCode(splitOrderItemInfo.getCityCode());
                mainOrder.setLoanCycle(splitOrderItemInfo.getLoanCycle());
                mainOrder.setDeliveryType(splitOrderItemInfo.getDeliveryType());
                mainOrder.setSeller(splitOrderItemInfo.getItemSku().getSeller());
                mainOrder.setWarehourseStockList(splitOrderItemInfo.getWarehourseStockList());
                // 子单计算
                SubOrder subOrder = new SubOrder();
                // 支付凭证
                subOrder.setCartId(splitOrderItemInfo.getCartId());
                // 商品基本信息
                subOrder.setItemSku(splitOrderItemInfo.getItemSku());
                subOrder.setOrderQty(splitOrderItemInfo.getItemQty());
                subOrder.setSkuQuoteId(splitOrderItemInfo.getItemSku().getSkuQuoteId());
                // 卖家信息
                subOrder.setSellerId(splitOrderItemInfo.getItemSku().getSeller().getSellerId());
                subOrder.setCategoryCommissionRate(splitOrderItemInfo.getItemSku().getCategoryCommissionRate());
                // 分期周期
                subOrder.setLoanCycle(splitOrderItemInfo.getLoanCycle());
                //物流方式
                subOrder.setDeliveryType(splitOrderItemInfo.getDeliveryType());
                subOrder.setItemDelivery(splitOrderItemInfo.getItemSku().getItemDelivery());
                supportDeliveryList = supportDeliveryList.stream()
                    .filter(splitOrderItemInfo.getItemSku().getSupportDeliveryList()::contains)
                    .collect(Collectors.toList());
                // 发货仓信息
                subOrder.setWarehourseStockList(splitOrderItemInfo.getWarehourseStockList());
                // 收货地址城市 --- 不一定是最终地址
                subOrder.setCityCode(splitOrderItemInfo.getCityCode());
                // 收货地址
                ReceiveAddr receiveAddr = new ReceiveAddr();
                if (Objects.nonNull(splitOrderItemInfo.getReceiver())) {
                    receiveAddr.setReceiverId(splitOrderItemInfo.getReceiver().getReceiverId());
                }
                subOrder.setReceiver(receiveAddr);
                // 扩展信息 重量 商品类型 是否可以退
                subOrder.orderAttr().setWeight(splitOrderItemInfo.getItemSku().getWeight());
                subOrder.orderAttr().setItemType(splitOrderItemInfo.getItemSku().getItemType());
                subOrder.orderAttr().setCanRefunds(splitOrderItemInfo.getItemSku().getCanRefunds());
                if(Objects.nonNull(splitOrderItemInfo.getItemSku().getSeller())) {
                    subOrder.orderAttr().setSellerPhone(splitOrderItemInfo.getItemSku().getSeller().getPhone());
                }
                if(Objects.nonNull(splitOrderItemInfo.getItemSku().getDeliveryType())){
                    // 物流信息
                    subOrder.orderAttr().setLogisticsType(splitOrderItemInfo.getItemSku().getDeliveryType());
                    mainOrder.orderAttr().setLogisticsType(splitOrderItemInfo.getItemSku().getDeliveryType());
                    if(DeliveryTypeEnum.SELLER_KA.getCode().equals(splitOrderItemInfo.getItemSku().getDeliveryType()))
                    {
                        subOrder.orderAttr().setSellerKa(true);
                        mainOrder.orderAttr().setSellerKa(true);
                    }
                } else {
                    //默认值
                    mainOrder.orderAttr().setLogisticsType(LogisticsTypeEnum.COURIER_LODOOR.getCode());
                    subOrder.orderAttr().setLogisticsType(LogisticsTypeEnum.COURIER_LODOOR.getCode());
                }

                // 设置主单数据
                mainOrder.setReceiver(receiveAddr);
                mainOrder.getSubOrders().add(subOrder);
                mainOrder.setSupportDeliveryList(supportDeliveryList);
            }
            splitMainOrder.add(mainOrder);
        }
        return splitMainOrder;
    }

    @Override
    public List<MainOrder> splitOrder(List<MainOrder> mainOrders) {
        List<MainOrder> logisticsType = new ArrayList<>();
        //邮寄方式拆单
        MainOrder mainOrder = null;
        for (int i = mainOrders.size(); i > 0; i--) {
            mainOrder = mainOrders.get(i - 1);
            if (mainOrder.getSubOrders().size() > 1) {
                //按照邮寄方式拆单
                logisticsType.addAll(splitMainOrderByLogisticsType(mainOrder));
            }
            else if (mainOrder.getSubOrders().size() == 1) {
                logisticsType.add(mainOrder);
            }
        }
        //按照地址拆单
        List<MainOrder> address = new ArrayList<>();
        for (int i = logisticsType.size(); i > 0; i--) {
            mainOrder = logisticsType.get(i - 1);
            if (mainOrder.getSubOrders().size() > 1) {
                //按照地址拆单,同一个地址拆到一个单子里边
                address.addAll(splitMainOrderByAddress(mainOrder));
            }
            else if (mainOrder.getSubOrders().size() == 1) {
                address.add(mainOrder);
            }
        }
        return address;
    }

    /**
     * 根据收货拆分订单，同一个地址的放一个订单
     * @param mainOrder
     * @return
     */
    private static List<MainOrder> splitMainOrderByAddress(MainOrder mainOrder) {
        // 用于存储新MainOrder的列表
        List<MainOrder> newMainOrders = new ArrayList<>();
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            Optional<MainOrder> bestMatch = newMainOrders.stream()
                .filter(newMainOrder -> hasSameAddress(newMainOrder, subOrder))
                .findFirst();
            if (bestMatch.isPresent()) {
                // 将当前SubOrder添加到匹配的MainOrder中
                bestMatch.get().getSubOrders().add(subOrder);
            } else {
                // 创建一个新的MainOrder
                MainOrder newMainOrder = new MainOrder();
                BeanUtil.copyProperties(mainOrder, newMainOrder);
                newMainOrder.getSubOrders().clear();
                newMainOrder.getSubOrders().add(subOrder);
                newMainOrders.add(newMainOrder);
            }
        }
        return newMainOrders;
    }

    /**
     * 是否有相同的地址
     * @param mainOrder
     * @param subOrder
     * @return
     */
    private static boolean hasSameAddress(MainOrder mainOrder, SubOrder subOrder) {
        ReceiveAddr receiveAddr = subOrder.getReceiver();
        for (SubOrder existingSubOrder : mainOrder.getSubOrders()) {
            if (existingSubOrder.getReceiver().getReceiverId().equals(receiveAddr.getReceiverId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据物流方式拆分订单，同一个物流方式的放一个订单
     * @param mainOrder
     * @return
     */
    private static List<MainOrder> splitMainOrderByLogisticsType(MainOrder mainOrder) {
        // 用于存储新MainOrder的列表
        List<MainOrder> newMainOrders = new ArrayList<>();
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            Optional<MainOrder> bestMatch = newMainOrders.stream()
                .filter(newMainOrder -> hasSameLogisticsType(newMainOrder, subOrder))
                .findFirst();
            if (bestMatch.isPresent()) {
                // 将当前SubOrder添加到匹配的MainOrder中
                bestMatch.get().getSubOrders().add(subOrder);
            } else {
                // 创建一个新的MainOrder
                MainOrder newMainOrder = new MainOrder();
                BeanUtil.copyProperties(mainOrder, newMainOrder);
                newMainOrder.getSubOrders().clear();
                newMainOrder.getSubOrders().add(subOrder);
                newMainOrders.add(newMainOrder);
            }
        }
        return newMainOrders;
    }

    /**
     * 是否有相同的物流方式
     * @param mainOrder
     * @param subOrder
     * @return
     */
    private static boolean hasSameLogisticsType(MainOrder mainOrder, SubOrder subOrder) {
        Integer deliveryType = subOrder.getDeliveryType();
        for (SubOrder existingSubOrder : mainOrder.getSubOrders()) {
            if (existingSubOrder.getDeliveryType().equals(deliveryType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据仓库拆分订单，优先同一个仓库最多商品放一起
     * @param mainOrder
     * @return
     */
    private static List<MainOrder> splitMainOrderByWarehouse(MainOrder mainOrder) {
        // 用于存储新MainOrder的列表
        List<MainOrder> newMainOrders = new ArrayList<>();

        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            Optional<MainOrder> bestMatch = newMainOrders.stream()
                .filter(newMainOrder -> hasSameWarehouse(newMainOrder, subOrder))
                .max(Comparator.comparingInt(o -> o.getSubOrders().size()));
            if (bestMatch.isPresent()) {
                // 将当前SubOrder添加到匹配的MainOrder中
                bestMatch.get().getSubOrders().add(subOrder);
            } else {
                // 创建一个新的MainOrder
                MainOrder newMainOrder = new MainOrder();
                newMainOrder.getSubOrders().add(subOrder);
                newMainOrders.add(newMainOrder);
            }
        }
        return newMainOrders;
    }

    /**
     * 是否有相同的仓储库
     * @param mainOrder
     * @param subOrder
     * @return
     */
    private static boolean hasSameWarehouse(MainOrder mainOrder, SubOrder subOrder) {
        Set<Long> currentWarehouses = new HashSet<>(subOrder.getItemSku().getWarehouseIdList());
        for (SubOrder existingSubOrder : mainOrder.getSubOrders()) {
            Set<Long> existingWarehouses = new HashSet<>(existingSubOrder.getItemSku().getWarehouseIdList());
            if (currentWarehouses.stream().anyMatch(existingWarehouses::contains)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成用于创建订单的token参数, 包含商品、收件人、优惠选择、价格计算、幂等ID生成
     * @param order
     * @param token
     * 2024-12-17 11:48:47
     */
    @Override
    public String getOrderToken(CreatingOrder order, String token) {
        CreatingOrder creatingOrder = new CreatingOrder();
        creatingOrder.setCartId(order.getCartId());
        creatingOrder.setLoan(order.getLoan());
        creatingOrder.setInstallment(order.getInstallment());
        creatingOrder.setReceiver(order.getReceiver());
        creatingOrder.setOrderPrice(order.getOrderPrice());
        creatingOrder.setPromotions(order.getPromotions());
        creatingOrder.setIsFromCart(order.getIsFromCart());
        creatingOrder.setCartType(order.getCartType());
        creatingOrder.setParams(order.getParams());
        creatingOrder.setMainOrders(order.getMainOrders().stream().map(main -> {
            MainOrder tmain = new MainOrder();
            tmain.setPromotions(main.getPromotions());
            tmain.setOrderPrice(main.getOrderPrice());
            tmain.setOrderChannel(main.getOrderChannel());
            tmain.setBizCodes(main.getBizCodes());
            tmain.setOrderType(main.getOrderType());
            tmain.setStepOrders(main.getStepOrders());
            tmain.setStepTemplate(main.getStepTemplate());
            tmain.setOrderAttr(main.getOrderAttr());
            tmain.setSubOrders(main.getSubOrders().stream().map(sub -> {
                ItemSku itemSku = new ItemSku();
                itemSku.setItemId(sub.getItemSku().getItemId());
                itemSku.setSkuId(sub.getItemSku().getSkuId());
                SubOrder tsub = new SubOrder();
                tsub.setOrderPrice(sub.getOrderPrice());
                tsub.setItemSku(itemSku);
                tsub.setOrderQty(sub.getOrderQty());
                return tsub;
            }).collect(Collectors.toList()));
            return tmain;
        }).collect(Collectors.toList()));
        // 幂等ID
        String key = "CREATE_ORDER_" + (StringUtils.isEmpty(token) ?  UUID.randomUUID() : token);
        cacheRepository.atomSetLong(key, LOCK_STATUS_1, LOCK_TIME_MINUTE, TimeUnit.MINUTES);
        creatingOrder.putExtra(EXT_IDEMPOTENT_KEY, key);
        String fullToken = JSON.toJSONString(creatingOrder);
        if (Boolean.TRUE.equals(useShortToken)) {
            String uuid = UUID.randomUUID().toString();
            cacheRepository.put(uuid, fullToken, 2, TimeUnit.HOURS);
            return uuid;
        }
        return fullToken;
    }

    /**
     * token信息拆包, 同时对幂等ID加锁
     * @param token
     * @return CreatingOrder
     * 2024-12-13 09:41:01
     */
    @Override
    public CreatingOrder unpackToken(String token) {
        if (Boolean.TRUE.equals(useShortToken)) {
            String fullToken = cacheRepository.get(token);
            if (StringUtils.isBlank(fullToken)) {
                throw new GmallException(OrderErrorCode.ORDER_TOKEN_EXPIRED);
            }
            token = fullToken;
        }
        CreatingOrder creatingOrder = JSON.parseObject(token, CreatingOrder.class);
        // 幂等ID
        String key = (String) creatingOrder.getExtra(EXT_IDEMPOTENT_KEY);
        if (key == null) {
            throw new GmallException(OrderErrorCode.ORDER_TOKEN_EXPIRED);
        }
        // 压测token, 不校验, 只调用一下redis
        if (PRESSURE_TEST_TOKEN.equals(key)) {
            cacheRepository.get(key);
            cacheRepository.compareAndSetLong(key, LOCK_STATUS_1, LOCK_STATUS_2, LOCK_TIME_MINUTE, TimeUnit.MINUTES);
            return creatingOrder;
        }
        Object object = cacheRepository.get(key);
        if (object == null) {
            throw new GmallException(OrderErrorCode.ORDER_TOKEN_EXPIRED);
        }
        if ((object instanceof Number) && ((Number) object).longValue() == LOCK_STATUS_3) {
            throw new GmallException(OrderErrorCode.ORDER_TOKEN_USED);
        }
        if (!cacheRepository.compareAndSetLong(key, LOCK_STATUS_1, LOCK_STATUS_2, LOCK_TIME_MINUTE, TimeUnit.MINUTES)) {
            throw new GmallException(OrderErrorCode.ORDER_TOKEN_LOCK_FAIL);
        }
        return creatingOrder;
    }

    /**
     * token信息拆包, 同时对幂等ID加锁
     * @param token
     * @return CreatingOrder
     * 2024-12-13 09:41:01
     */
    @Override
    public CreatingOrder unpackTokenCache(String token) {
        if (Boolean.TRUE.equals(useShortToken)) {
            String fullToken = cacheRepository.get(token);
            if (StringUtils.isBlank(fullToken)) {
               return null;
            }
            token = fullToken;
        }
        try {
            CreatingOrder creatingOrder = JSON.parseObject(token, CreatingOrder.class);
            return creatingOrder;
        }catch (Exception e) {

        }
        return null;
    }

    @Override
    public void recoverToken(CreatingOrder order, boolean available) {
        String key = (String) order.getExtra(EXT_IDEMPOTENT_KEY);
        if (key == null) {
            return;
        }
        if (available) {
            cacheRepository.atomSetLong(key, LOCK_STATUS_1, LOCK_TIME_MINUTE, TimeUnit.MINUTES);
        } else {
            //cacheRepository.delete(key);
            cacheRepository.atomSetLong(key, LOCK_STATUS_3, LOCK_TIME_MINUTE, TimeUnit.MINUTES);
        }
    }

    @Override
    public void saveOrder(CreatingOrder order) {
        orderSaveAbility.saveOrder(order);
    }

    @Override
    public void saveOrderForConfirm(CreatingOrder order) {
         orderSaveAbility.saveOrderForConfirm(order);
    }
}
