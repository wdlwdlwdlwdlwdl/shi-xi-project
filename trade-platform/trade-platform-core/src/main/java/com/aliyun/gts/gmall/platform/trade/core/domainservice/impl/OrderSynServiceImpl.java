package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.center.misc.api.dto.input.city.CityQueryRpcReq;
import com.aliyun.gts.gmall.center.misc.api.dto.output.city.CityDTO;
import com.aliyun.gts.gmall.center.misc.api.facade.city.CityReadFacade;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.*;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.SynDeliveryMsgFacade;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.IcWarehouseRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.SellerConfirmWriteRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderSynService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Customer;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class OrderSynServiceImpl implements OrderSynService {

    @Autowired
    SynDeliveryMsgFacade synDeliveryMsgFacade;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    protected MultiLangConverter multiLangConverter;

    @Autowired
    private CityReadFacade cityReadFacade;

    private static final String COUNTRY_CODE = "7";
    private static final String FILLMENT_TYPE_PVZ = "PHYSICAL_PVZ";
    private static final String FILLMENT_TYPE_TASTAMAT = "PHYSICAL_TASTAMAT";
    private static final String FILLMENT_TYPE_PHYSICAL_SHIP = "PHYSICAL_SHIP";
    private static HashMap<String,String> cityCache = new HashMap<>();
    private final static String IS_DC = "2";

    @Override
    public void synCreateOrder(MainOrder mainOrder, SellerConfirmWriteRpcReq req) {
        Customer c = userRepository.getCustomerRequired(mainOrder.getCustomer().getCustId());
        mainOrder.setCustomer(c);
        CreateOrderReq createOrderReq = new CreateOrderReq();
        createOrderReq.setId(mainOrder.getPrimaryOrderId().intValue());
        createOrderReq.setType("ORDER_ACCEPTED");
        createOrderReq.setPayload(extractedCreateParam(mainOrder,req));
        synDeliveryMsgFacade.synCreateOrder(createOrderReq);
    }

    private static PayloadReq extractedCreateParam(MainOrder mainOrder,SellerConfirmWriteRpcReq req) {
        PayloadReq payloadReq = new PayloadReq();
        LogisticsReq from = new LogisticsReq();
        if(req.getWarehouse() != null){
            from.setAddressLine1(req.getWarehouse().getCity());
            from.setAddressLine2(req.getWarehouse().getCity());
            from.setAddressLine3(req.getWarehouse().getAddress());
            from.setLatitude(req.getWarehouse().getLatitude().doubleValue());
            from.setLongitude(req.getWarehouse().getLongitude().doubleValue());
            PhoneReq phonePrimary= new PhoneReq();
            phonePrimary.setPhoneNumber(req.getWarehouse().getTelephone());
            phonePrimary.setCountryCode(req.getWarehouse().getCountry());
            from.setPhonePrimary(phonePrimary);
            CityReq city = new CityReq();
            city.setCode(req.getWarehouse().getCity());
            city.setName(req.getWarehouse().getName());
            from.setCity(city);
        }
        payloadReq.setFrom(from);
        LogisticsReq to = new LogisticsReq();
        to.setAddressLine1(mainOrder.getReceiver().getDeliveryAddr());
        to.setAddressLine2(mainOrder.getReceiver().getStreetCode());
        to.setAddressLine3(mainOrder.getReceiver().getDeliveryAddr());
        to.setFirstName(mainOrder.getReceiver().getReceiverName());
        to.setLastName(mainOrder.getReceiver().getReceiverName());
        CityReq toCity = new CityReq();
        toCity.setCode(mainOrder.getReceiver().getCityCode());
        toCity.setName(mainOrder.getReceiver().getDeliveryAddr());
        to.setCity(toCity);
        payloadReq.setFrom(from);
        payloadReq.setTo(to);
        CustomerReq customerReq = new CustomerReq();
        customerReq.setId(mainOrder.getCustomer().getCustId().intValue());
        customerReq.setEmail(mainOrder.getCustomer().getEmail());
        customerReq.setIin(mainOrder.getCustomer().getIin());
        PhoneReq phoneReq = new PhoneReq();
        phoneReq.setPhoneNumber(mainOrder.getCustomer().getPhone());
        customerReq.setPhoneActive(phoneReq);
        customerReq.setFirstName(mainOrder.getCustomer().getFirstName());
        customerReq.setLastName(mainOrder.getCustomer().getLastName());

        payloadReq.setCustomer(customerReq);
        MerchantReq merchantReq = new MerchantReq();
        merchantReq.setId(mainOrder.getSeller().getSellerId());
        merchantReq.setName(mainOrder.getSeller().getSellerName());
        merchantReq.setCode(String.valueOf(mainOrder.getSeller().getSellerId()));
        merchantReq.setPhoneStr(mainOrder.getSeller().getPhone());
        merchantReq.setEmail(mainOrder.getSeller().getEmail());
        payloadReq.setMerchant(merchantReq);
        List<ItemReq> itemList = new ArrayList<>();
        ItemReq itemReq = new ItemReq();
        for(SubOrder subOrder: mainOrder.getSubOrders()){
            itemReq.setItemId(subOrder.getItemSku().getItemId());
            itemReq.setName(subOrder.getItemSku().getItemTitle());
            itemReq.setCost(subOrder.getItemSku().getItemPrice().getItemPrice());
            itemReq.setCategoryCode(String.valueOf(subOrder.getItemSku().getCategoryId()));
            itemList.add(itemReq);
        }
        payloadReq.setItems(itemList);
        OrderReq order = new OrderReq();
        order.setOrderId(mainOrder.getPrimaryOrderId());
        order.setOrderNumber(String.valueOf(mainOrder.getPrimaryOrderId()));
        payloadReq.setOrder(order);
        return payloadReq;
    }

    @Override
    public void synConfirmOrder(TcOrderDO tcOrderDO, SellerConfirmWriteRpcReq req) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(tcOrderDO.getPrimaryOrderId());
        if (mainOrder == null) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        Customer c = userRepository.getCustomerRequired(tcOrderDO.getCustId());
        mainOrder.setCustomer(c);
        ConfirmOrderReq confirmOrderReq = new ConfirmOrderReq();
        confirmOrderReq.setId(tcOrderDO.getPrimaryOrderId().intValue());
        confirmOrderReq.setType("ORDER_ACCEPTED");
        confirmOrderReq.setPayload(extractedAccpetParam(mainOrder,req));
        String jsonString = JSON.toJSONString(confirmOrderReq);
        synDeliveryMsgFacade.synConfirmOrder(confirmOrderReq);
    }

    @Override
    public void deliveryConfirm(MainOrder mainOrder, SellerConfirmWriteRpcReq req) {
        Customer customer = userRepository.getCustomerRequired(mainOrder.getCustomer().getCustId());
        mainOrder.setCustomer(customer);
        Seller seller = userRepository.getSeller(mainOrder.getSeller().getSellerId());
        mainOrder.setSeller(seller);
        if(cityCache.isEmpty()){
            CityQueryRpcReq rpc = new CityQueryRpcReq();
            RpcResponse<List<CityDTO>> list = cityReadFacade.queryCityAll(rpc);
            if(list.isSuccess()){
                for(CityDTO dto:list.getData()){
                    cityCache.put(dto.getCityCode(),dto.getPostCode());
                }
            }
        }
        synDeliveryMsgFacade.deliveryConfirm(extractedParam(mainOrder,req));
    }

    private static PayloadConfirmReq extractedAccpetParam(MainOrder mainOrder,SellerConfirmWriteRpcReq req) {
        PayloadConfirmReq payloadReq = new PayloadConfirmReq();
        payloadReq.setOrderNumber(String.valueOf(mainOrder.getPrimaryOrderId()));
        LogisticsReq address = new LogisticsReq();
        CityReq city = new CityReq();

        if(req.getWarehouse() != null){
            address.setAddressLine1(req.getWarehouse().getCity());
            address.setAddressLine2(req.getWarehouse().getCity());
            address.setAddressLine3(req.getWarehouse().getAddress());
            address.setLatitude(req.getWarehouse().getLatitude().doubleValue());
            address.setLongitude(req.getWarehouse().getLongitude().doubleValue());
            PhoneReq phonePrimary= new PhoneReq();
            phonePrimary.setPhoneNumber(req.getWarehouse().getTelephone());
            phonePrimary.setCountryCode(req.getWarehouse().getCountry());
            address.setPhonePrimary(phonePrimary);
            city.setCode(req.getWarehouse().getCity());
            city.setName(req.getWarehouse().getName());
            address.setCity(city);
        }
        payloadReq.setAddress(address);
        return payloadReq;
    }

    private  DeliveryReq extractedParam(MainOrder mainOrder,SellerConfirmWriteRpcReq req) {
        DeliveryReq deliveryReq = new DeliveryReq();
        fromMessage(req, deliveryReq);
        customerMessage(mainOrder, deliveryReq);
        merchantMessage(mainOrder, deliveryReq);
        itemMessage(mainOrder, deliveryReq);
        OrderReq order = new OrderReq();
        order.setOrderId(mainOrder.getPrimaryOrderId());
        order.setOrderNumber(String.valueOf(mainOrder.getPrimaryOrderId()));
        order.setPointOfServiceCode(String.valueOf(mainOrder.getSeller().getSellerId()));
        deliveryReq.setOrder(order);
        PickupPointReq pvz = new PickupPointReq();
        TastamatPointReq tastamatPointReq = new TastamatPointReq();
        if(Objects.equals(mainOrder.orderAttr().getLogisticsType(), LogisticsTypeEnum.PVZ.getCode())){
            pvz.setPickUpPointId(mainOrder.getReceiver().getRefAddressId());
            pvz.setLatitude(mainOrder.getReceiver().getLatitude());
            pvz.setLongitude(mainOrder.getReceiver().getLongitude());
            pvz.setCityCode(mainOrder.getReceiver().getCityCode());
            pvz.setPhoneNumber(mainOrder.getReceiver().getPhone());
            deliveryReq.setFulFillmentType(FILLMENT_TYPE_PVZ);
            deliveryReq.setPickupPoint(pvz);
            deliveryReq.setTastamatPoint(null);
        }else if(Objects.equals(mainOrder.orderAttr().getLogisticsType(), LogisticsTypeEnum.POSTAMAT.getCode())){
            tastamatPointReq.setLockerIndex(String.valueOf(mainOrder.getReceiver().getRefAddressId()));
            tastamatPointReq.setSize("L");
            deliveryReq.setFulFillmentType(FILLMENT_TYPE_TASTAMAT);
            deliveryReq.setTastamatPoint(tastamatPointReq);
            deliveryReq.setPickupPoint(null);
        }else{
            toMessage(mainOrder, req, deliveryReq);
            deliveryReq.setFulFillmentType(FILLMENT_TYPE_PHYSICAL_SHIP);
            deliveryReq.setPickupPoint(null);
            deliveryReq.setTastamatPoint(null);
        }
        if(req.getWarehouse() != null){
            if(IS_DC.equals(req.getWarehouse().getIsDc())){
                deliveryReq.setThroughDistributionCenter(true);
            }
        }
        deliveryReq.setSelfMerchantDelivery(false);
        return deliveryReq;
    }

    private void itemMessage(MainOrder mainOrder, DeliveryReq deliveryReq) {
        List<ItemReq> itemList = new ArrayList<>();
        ItemReq itemReq = new ItemReq();
        for(SubOrder subOrder: mainOrder.getSubOrders()){
            itemReq.setItemId(subOrder.getItemSku().getItemId());
            itemReq.setName(multiLangConverter.mText_to_str(multiLangConverter.to_multiLangText(subOrder.getItemSku().getItemTitle())));
            itemReq.setCost(subOrder.getItemSku().getItemPrice().getItemPrice());
            //itemReq.setCategoryCode(String.valueOf(subOrder.getItemSku().getCategoryId()));
            itemReq.setCategoryCode(String.valueOf(subOrder.getItemSku().getCategoryName()));
            itemReq.setQuantity(subOrder.getOrderQty());
            Properties properties = new Properties();
            if(subOrder.getItemSku().getWeight()!=null){
                properties.setWeight(Double.valueOf(subOrder.getItemSku().getWeight()));
            }else{
                properties.setWeight(Double.valueOf("100"));
            }
            properties.setHeight(null);
            properties.setLength(null);
            properties.setWidth(null);
            itemReq.setProperties(properties);
            itemList.add(itemReq);
        }
        deliveryReq.setPlaceQuantity(itemList.size());
        deliveryReq.setItems(itemList);
    }

    private  void merchantMessage(MainOrder mainOrder, DeliveryReq deliveryReq) {
        MerchantReq merchantReq = new MerchantReq();
        merchantReq.setId(mainOrder.getSeller().getSellerId());
        merchantReq.setName(mainOrder.getSeller().getSellerName());
        merchantReq.setCode(String.valueOf(mainOrder.getSeller().getSellerId()));
        if(mainOrder.getSeller().getPhone()!=null){
            merchantReq.setPhoneStr(mainOrder.getSeller().getPhone());
        }else{
            merchantReq.setPhoneStr("");
        }
        if(mainOrder.getSeller().getEmail()!=null){
            merchantReq.setEmail(mainOrder.getSeller().getEmail());
        }else{
            merchantReq.setEmail("");
        }
        deliveryReq.setMerchant(merchantReq);
    }

    private  void customerMessage(MainOrder mainOrder, DeliveryReq deliveryReq) {
        CustomerReq customerReq = new CustomerReq();
        customerReq.setId(mainOrder.getCustomer().getCustId().intValue());
        customerReq.setEmail(mainOrder.getCustomer().getEmail());
        customerReq.setIin(mainOrder.getCustomer().getIin());
        PhoneReq phoneReq = new PhoneReq();
        phoneReq.setPhoneNumber(mainOrder.getCustomer().getPhone());
        if(mainOrder.getCustomer().getCountryCode()!=null){
            phoneReq.setCountryCode(mainOrder.getCustomer().getCountryCode());
        }else{
            phoneReq.setCountryCode(COUNTRY_CODE);
        }
        customerReq.setPhoneActive(phoneReq);
        customerReq.setFirstName(mainOrder.getCustomer().getFirstName());
        customerReq.setLastName(mainOrder.getCustomer().getLastName());

        deliveryReq.setCustomer(customerReq);
    }

    private  void toMessage(MainOrder mainOrder, SellerConfirmWriteRpcReq req, DeliveryReq deliveryReq) {
        LogisticsReq to = new LogisticsReq();
        String[] address = null;
        if (mainOrder.getReceiver().getDeliveryAddr() != null && !mainOrder.getReceiver().getDeliveryAddr().isEmpty()) {
            address = mainOrder.getReceiver().getDeliveryAddr().split(",");
            if(address.length>=1){
                to.setAddressLine1(address[0]);
                to.setAddressLine2(address[1]);
                to.setAddressLine3(address[2]);
            }else{
                to.setAddressLine1(mainOrder.getReceiver().getDeliveryAddr());
                to.setAddressLine2(mainOrder.getReceiver().getDeliveryAddr());
                to.setAddressLine3("");
            }
        }
        to.setFirstName(mainOrder.getFirstName());
        to.setLastName(mainOrder.getLastName());
        to.setEmail(mainOrder.getReceiver().getEmail());
        to.setCompanyName(mainOrder.getReceiver().getCompanyName());
        CityReq toCity = new CityReq();
        toCity.setCode(mainOrder.getReceiver().getCityCode());
        toCity.setName(mainOrder.getReceiver().getCity());
        toCity.setPostalCode(mainOrder.getReceiver().getPostCode());
        if(mainOrder.getReceiver().getPostCode()==null){
            toCity.setPostalCode(cityCache.get(mainOrder.getReceiver().getCityCode()));
            to.setPostalCode(cityCache.get(mainOrder.getReceiver().getCityCode()));
        }else{
            toCity.setPostalCode(mainOrder.getReceiver().getPostCode());
        }
        if(mainOrder.getReceiver().getLatitude()!=null){
            to.setLatitude(Double.parseDouble(mainOrder.getReceiver().getLatitude()));
        }
        if(mainOrder.getReceiver().getLongitude()!=null){
            to.setLongitude(Double.parseDouble(mainOrder.getReceiver().getLongitude()));
        }
        PhoneReq toPhoneReq = new PhoneReq();
        toPhoneReq.setPhoneNumber(mainOrder.getReceiver().getPhone());
        if(mainOrder.getReceiver().getCountryCode()!=null){
            toPhoneReq.setCountryCode(COUNTRY_CODE);
            //toPhoneReq.setCountryCode(mainOrder.getReceiver().getCountryCode());
        }else{
            toPhoneReq.setCountryCode(COUNTRY_CODE);
        }
        to.setPhonePrimary(toPhoneReq);
        to.setPhoneSecondary(null);
        to.setCity(toCity);
        deliveryReq.setTo(to);
    }

    private  void fromMessage(SellerConfirmWriteRpcReq req, DeliveryReq deliveryReq) {
        LogisticsReq from = new LogisticsReq();
        if(req.getWarehouse() != null){
            String[] address = null;
            if (req.getWarehouse().getAddress() != null && !req.getWarehouse().getAddress().isEmpty()) {
                address = req.getWarehouse().getAddress().split(",");
                if(address.length == 2){
                    from.setAddressLine1(address[0]);
                    from.setAddressLine2(address[1]);
                }else if(address.length == 3){
                    from.setAddressLine1(address[0]);
                    from.setAddressLine2(address[1]);
                    from.setAddressLine3(address[2]);
                }
                else{
                    from.setAddressLine1(req.getWarehouse().getAddress());
                    from.setAddressLine2(req.getWarehouse().getAddress());
                    from.setAddressLine3("");
                }
            }
            from.setLatitude(req.getWarehouse().getLatitude());
            from.setLongitude(req.getWarehouse().getLongitude());
            PhoneReq phonePrimary = new PhoneReq();
            phonePrimary.setPhoneNumber(req.getWarehouse().getTelephone());
            if(req.getWarehouse().getCountry()!=null){
                phonePrimary.setCountryCode(COUNTRY_CODE);
                //phonePrimary.setCountryCode(req.getWarehouse().getCountry());
            }else{
                phonePrimary.setCountryCode(COUNTRY_CODE);
            }

            CityReq city = new CityReq();
            city.setCode(req.getWarehouse().getCityCode());
            city.setName(req.getWarehouse().getCity());
            if(req.getWarehouse().getPostalCode()==null){
                city.setPostalCode(cityCache.get(req.getWarehouse().getCityCode()));
                from.setPostalCode(cityCache.get(req.getWarehouse().getCityCode()));
            }else{
                city.setPostalCode(req.getWarehouse().getPostalCode());
            }
            //临时造数据
            if(req.getWarehouse().getIsDc().equals("2")){
                from.setLatitude(43.230437);
                from.setLongitude(76.894594);
                city.setCode("750000000");
                city.setName("Almaty");
                city.setPostalCode("050000");
                phonePrimary.setPhoneNumber("77078808816");
            }
            from.setPhonePrimary(phonePrimary);
            from.setComment(null);
            from.setCity(city);
        }
        deliveryReq.setFrom(from);
    }

    public static void main(String[] args) {
        LogisticsReq from = new LogisticsReq();
        SellerConfirmWriteRpcReq req = new SellerConfirmWriteRpcReq();
        IcWarehouseRpcReq ware = new IcWarehouseRpcReq();
        ware.setAddress(" Алматы    Алматы, проспект Райымбека, 165А");
        req.setWarehouse(ware);
        String[] address = null;
        if (req.getWarehouse().getAddress() != null && !req.getWarehouse().getAddress().isEmpty()) {
            address = req.getWarehouse().getAddress().split(",");
            if(address.length == 2){
                from.setAddressLine1(address[0]);
                from.setAddressLine2(address[1]);
            }else if(address.length == 3){
                from.setAddressLine1(address[0]);
                from.setAddressLine2(address[1]);
                from.setAddressLine3(address[2]);
            }else{
                from.setAddressLine1(req.getWarehouse().getAddress());
                from.setAddressLine2(req.getWarehouse().getAddress());
                from.setAddressLine3("");
            }
        }
    }
}
