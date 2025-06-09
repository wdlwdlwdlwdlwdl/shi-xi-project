package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.operator.api.dto.input.OperatorByOutIdQuery;
import com.aliyun.gts.gmall.platform.operator.api.facade.OperatorReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Customer;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerAccountInfo;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Shop;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.util.RpcUtils;
import com.aliyun.gts.gmall.platform.user.api.dto.contants.CustomerStatusEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.contants.UserExtendConstants;
import com.aliyun.gts.gmall.platform.user.api.dto.input.*;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerExtendDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.ShopConfigDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerReadFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "userRepository", havingValue = "default", matchIfMissing = true)
public class UserRepositoryImpl implements UserRepository {
    private static final String EXT_SHOP_CONF = "shop_config";
    private static final String EXT_SHOP_0 = "shop_0";
    private static final String EXT_SHOP_NAME = "name";

    @Autowired
    private SellerReadFacade sellerReadFacade;

    @Autowired
    private CustomerReadFacade customerReadFacade;

    @Autowired
    private OperatorReadFacade operatorReadFacade;

    @Override
    public Customer getCustomerRequired(Long custId) {
        CustomerQueryOption opt = new CustomerQueryOption();
        opt.setNeedCustExtends(false);
        opt.setNeedDefaultAddress(false);
        //opt.setNeedLoginAccount(false);
        CustomerByIdQuery customerByIdQuery = new CustomerByIdQuery();
        customerByIdQuery.setId(custId);
        customerByIdQuery.setOption(opt);
        RpcResponse<CustomerDTO> resp = RpcUtils.invokeRpc(
            () -> customerReadFacade.query(customerByIdQuery),
            "customerReadFacade.query",
            I18NMessageUtils.getMessage("user.query"),
            customerByIdQuery
        );  //# "用户查询"

        CustomerDTO cust = resp.getData();
        if (cust == null) {
            throw new GmallException(OrderErrorCode.USER_NOT_EXISTS);
        }
        if (!CustomerStatusEnum.NORMAL.getCode().toString().equals(cust.getStatus())) {
            throw new GmallException(OrderErrorCode.USER_STATUS_ILLEGAL);
        }
        Customer result = new Customer();
        result.setCustId(custId);
        result.setCustName(StringUtils.isNotBlank(cust.getNickname()) ? cust.getNickname() : cust.getUsername());
        result.setEmail(cust.getEmail());
        result.setIin(cust.getIin());
        result.setPhone(cust.getPhone());
        result.setFirstName(cust.getFirstName());
        result.setLastName(cust.getLastName());
        result.setBirthDay(cust.getBirthDay());
        result.setLanguage(cust.getLanguage());
        return result;
    }

    @Override
    public Seller getSeller(Long sellerId) {
        CommonByIdQuery commonByIdQuery = new CommonByIdQuery();
        commonByIdQuery.setId(sellerId);
        RpcResponse<SellerDTO> resp = RpcUtils.invokeRpc(() ->
            sellerReadFacade.query(commonByIdQuery),
            "sellerReadFacade.query",
            I18NMessageUtils.getMessage("seller.query"),
            commonByIdQuery
        );
        //# "卖家查询"
        SellerDTO seller = resp.getData();
        if (seller == null) {
            return null;
        }
        Map<String, Map<String, String>> extMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(seller.getSellerExtends())) {
            for (SellerExtendDTO ext : seller.getSellerExtends()) {
                if (ext != null) {
                    CommUtils.getValue(extMap, ext.getType(), HashMap::new).put(ext.getK(), ext.getV());
                }
            }
        }
        SellerFundAccount sellerFundAccount = Optional.ofNullable(extMap.get(UserExtendConstants.APPLY_INFO))
            .map(conf -> conf.get(UserExtendConstants.APPLY_INFO))
            .map(x -> JSONObject.parseObject(x, SellerApplyInfo.class))
            .map(SellerApplyInfo::getSellerFundAccount)
            .orElse(null);

        SellerAccountInfo sellerAccountInfo = new SellerAccountInfo();
        sellerAccountInfo.setExtendAccountInfos(seller.getFeatures());
        if (sellerFundAccount != null) {
            sellerAccountInfo.setAlipayAccountNo(sellerFundAccount.getAlipayAccountNo());
            sellerAccountInfo.setWechatAccountNo(sellerFundAccount.getWechatAccountNo());
        }
        String sellerName = seller.getUsername();
        if (StringUtils.isNotBlank(seller.getNickname())) {
            sellerName = seller.getNickname();
        }
        String shopName = null;
        try {
            shopName = Optional.ofNullable(extMap.get(EXT_SHOP_CONF))
                .map(conf -> conf.get(EXT_SHOP_0))
                .map(JSON::parseObject)
                .map(json -> json.getString(EXT_SHOP_NAME))
                .orElse(null);
        } catch (Exception e) {
            // ignore;
        }
        Seller result = new Seller();
        result.setSellerId(sellerId);
        result.setSellerStatus(seller.getSellerStatus());
        result.setFeatures(seller.getFeatures());
        result.setSellerExtends(extMap);
        result.setPhone(seller.getPhone());
        result.setEmail(seller.getEmail());
        result.setShopName(shopName);
        result.setSellerName(sellerName);
        result.setBinOrIin(seller.getBinOrIin());
        result.setHeadUrl(seller.getHeadUrl());
        result.setSellerAccountInfo(sellerAccountInfo);
        result.setAddress(seller.getCompanyAddress());
        return result;
    }

    private String getSellerAccount(Map<String, String> sellerFeatures, String key) {
        if (MapUtils.isEmpty(sellerFeatures)) {
            return null;
        }
        return sellerFeatures.get(key);
    }

    @Override
    public List<Seller> getSellers(Collection<Long> sellerIds) {
        if (!(sellerIds instanceof Set)) {
            sellerIds = new HashSet<>(sellerIds);
        }
        List<Seller> list = new ArrayList<>();
        for (Long id : sellerIds) {
            Seller slr = getSeller(id);
            if (slr != null) {
                list.add(slr);
            }
        }
        return list;
    }

    /**
     * 批量查卖家，仅返回存在的卖家，无序且去重
     */
    @Override
    public List<Seller> getSellerByIds(List<Long> sellerIds) {
        CommonByBatchIdsQuery commonByBatchIdsQuery = new CommonByBatchIdsQuery();
        commonByBatchIdsQuery.setIds(sellerIds);
        RpcResponse<List<SellerDTO>> resp = RpcUtils.invokeRpc(() ->
            sellerReadFacade.queryByIds(commonByBatchIdsQuery),
            "sellerReadFacade.query",
            I18NMessageUtils.getMessage("seller.query"),
            commonByBatchIdsQuery
        );
        List<Seller> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(resp.getData())) {
            return result;
        }
        //# "卖家查询"
        List<SellerDTO> sellers = resp.getData();
        for (SellerDTO sellerDTO : sellers) {
            if (sellerDTO == null) {
                continue;
            }
            Map<String, Map<String, String>> extMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(sellerDTO.getSellerExtends())) {
                for (SellerExtendDTO ext : sellerDTO.getSellerExtends()) {
                    if (ext != null) {
                        CommUtils.getValue(extMap, ext.getType(), HashMap::new).put(ext.getK(), ext.getV());
                    }
                }
            }
            String sellerName = StringUtils.isNotBlank(sellerDTO.getNickname()) ?
                sellerDTO.getNickname() : sellerDTO.getUsername();
            Seller seller = new Seller();
            seller.setSellerId(sellerDTO.getId());
            seller.setSellerStatus(sellerDTO.getSellerStatus());
            seller.setFeatures(sellerDTO.getFeatures());
            seller.setSellerExtends(extMap);
            seller.setSellerName(sellerName);
            seller.setBinOrIin(sellerDTO.getBinOrIin());
            seller.setSellerAccountInfo(new SellerAccountInfo());
            seller.setPhone(sellerDTO.getPhone());
            result.add(seller);
        }
        return result;
    }


    @Override
    public Shop getSellerShop(Long sellerId) {
        Shop shop = new Shop();
        // 先查询门店
        ShopConfigQuery shopConfigQuery = new ShopConfigQuery();
        shopConfigQuery.setSellerId(sellerId);
        RpcResponse<ShopConfigDTO> resp = RpcUtils.invokeRpc(() ->
            sellerReadFacade.queryShop(shopConfigQuery),
            "sellerReadFacade.queryShop",
            I18NMessageUtils.getMessage("seller.query"),
            shopConfigQuery
        );
        if(Objects.nonNull(resp) && Objects.nonNull(resp.getData())){
            ShopConfigDTO shopConfigDTO = resp.getData();
            shop.setName(shopConfigDTO.getName());
            shop.setAddress(shopConfigDTO.getAddress());
            shop.setLogoUrl(shopConfigDTO.getLogoUrl());
            shop.setContactInformation(shopConfigDTO.getContactInformation());
        }
        // 查询卖家
        CommonByIdQuery commonByIdQuery = new CommonByIdQuery();
        commonByIdQuery.setId(sellerId);
        RpcResponse<SellerDTO> sellerRpc = RpcUtils.invokeRpc(() ->
            sellerReadFacade.query(commonByIdQuery),
            "sellerReadFacade.query",
            I18NMessageUtils.getMessage("seller.query"),
            commonByIdQuery
        );
        if(Objects.nonNull(sellerRpc) && Objects.nonNull(sellerRpc.getData())){
            shop.setContactPhone(sellerRpc.getData().getPhone());
        }
        return shop;
    }

    /**
     * 查询主账号语言
     */
    @Override
    public String queryMainOperatorLang(Long sellerId) {
        OperatorByOutIdQuery operatorByOutIdQuery = new OperatorByOutIdQuery();
        operatorByOutIdQuery.setOutId(sellerId);
        RpcResponse<String> resp = RpcUtils.invokeRpc(() ->
            operatorReadFacade.queryMainOperatorLang(operatorByOutIdQuery),
            "sellerReadFacade.queryMainOperatorLang",
            I18NMessageUtils.getMessage("seller.query"),
            operatorByOutIdQuery
        );
        if(Objects.nonNull(resp) && Objects.nonNull(resp.getData())){
            return resp.getData();
        }
        return null;
    }
}
