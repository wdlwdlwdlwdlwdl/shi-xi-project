package com.aliyun.gts.gmall.manager.front.customer.facade.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.user.api.dto.constants.CustomerSellerRelationFeatures;
import com.aliyun.gts.gmall.center.user.api.dto.constants.CustomerSellerRelationState;
import com.aliyun.gts.gmall.center.user.api.dto.constants.CustomerSellerRelationTags;
import com.aliyun.gts.gmall.center.user.api.dto.output.SellerIndicatorDTO;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.biz.adapter.DictAdapter;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.biz.output.DictVO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.CustomerAdapter;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.SellerOperatorAdapter;
import com.aliyun.gts.gmall.manager.front.customer.dto.inner.MallConfigDTO;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.ShopVipJoinCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.ShopVipQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.ShopVipVO;
import com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.customer.facade.ShopVipFacade;
import com.aliyun.gts.gmall.platform.user.api.dto.common.CustomerApplyExtendDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.contants.CustomerStatusEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerSellerRelationCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerSellerRelationDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.ShopConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ShopVipFacadeImpl implements ShopVipFacade {

    @Autowired
    private CustomerAdapter customerAdapter;
    @Autowired
    private SellerOperatorAdapter sellerOperatorAdapter;
    @Autowired
    private DictAdapter dictAdapter;

    @Override
    public ShopVipVO queryShopVip(ShopVipQuery query) {
        CustDTO user = UserHolder.getUser();
        CheckContext c = new CheckContext();
        ShopVipVO ret = null;
        if (user != null) {
            ret = checkJoin(user.getCustId(), query.getSellerId(), c);
        } else {
            ret = new ShopVipVO();  // 未登录
        }
        fillShopInfo(ret, query.getSellerId());
        if (ret.isCanJoinBVip() || ret.isCanJoinCVip()) {
            // 可加入会员时, 才提供用户信息
            fillCustInfo(ret, c.cust);
        }
        return ret;
    }

    private ShopVipVO checkJoin(Long custId, Long sellerId, CheckContext ctx) {
        CustomerSellerRelationDTO rel = customerAdapter.queryCustomerSellerRel(custId, sellerId);
        ctx.rel = rel;
        List<String> tags = ctx.getRelTags();
        ShopVipVO ret = new ShopVipVO();
        if (tags.contains(CustomerSellerRelationTags.B_VIP)) {
            ret.setBVip(true);
        }
        if (tags.contains(CustomerSellerRelationTags.C_VIP)) {
            ret.setCVip(true);
        }
        if (ret.isBVip() || ret.isCVip()) {
            return ret; // 目前只允许加入一种会员
        }

        // 店铺是否开启会员功能
        boolean joinCVipEnabled = true;
        boolean joinBVipEnabled = true;
        ShopConfigDTO shop = sellerOperatorAdapter.queryShopById(sellerId);
        if (shop == null || !Boolean.TRUE.equals(shop.getShopCVipEnabled())) {
            joinCVipEnabled = false;
        }
        if (shop == null || !Boolean.TRUE.equals(shop.getShopBVipEnabled())) {
            joinBVipEnabled = false;
        }
        if (!joinCVipEnabled && !joinBVipEnabled) {
            return ret; // 不可加入
        }

        // 平台是否开启会员功能
        DictVO dictVO = dictAdapter.queryByKeyWithCache(MallConfigDTO.DICT_KEY);
        if (dictVO == null || StringUtils.isBlank(dictVO.getDictValue())) {
            return ret; // 不可加入
        }
        MallConfigDTO mallConfig = JSON.parseObject(dictVO.getDictValue(), MallConfigDTO.class);
        if (!Boolean.TRUE.equals(mallConfig.getShopCVipEnabled())) {
            joinCVipEnabled = false;
        }
        if (!Boolean.TRUE.equals(mallConfig.getShopBVipEnabled())) {
            joinBVipEnabled = false;
        }
        if (!joinCVipEnabled && !joinBVipEnabled) {
            return ret; // 不可加入
        }

        // 根据用户身份 b or c
        CustomerDTO customer = customerAdapter.queryById(custId);
        ctx.cust = customer;
        if (customer == null) {
            throw new GmallException(CustomerFrontResponseCode.CUSTOMER_QUERY_FAIL);
        }
        if (!CustomerStatusEnum.NORMAL.getCode().toString().equals(customer.getStatus())) {
            // 未入驻完成的, 不可加入
            return ret;
        }

        if (customer.isB2b() && joinBVipEnabled) {
            ret.setCanJoinBVip(true);
        } else if (joinCVipEnabled) {
            ret.setCanJoinCVip(true);
        }
        return ret;
    }

    private void fillShopInfo(ShopVipVO ret, Long sellerId) {
        ShopConfigDTO shop = sellerOperatorAdapter.queryShopById(sellerId);
        SellerDTO seller = sellerOperatorAdapter.querySellerById(sellerId);
        if (seller == null || shop == null) {
            // shop 一定非null (user center 给了默认shop), 所以查seller 判断
            throw new GmallException(CustomerFrontResponseCode.SHOP_NOT_EXIST);
        }
        ret.setShopLogo(shop.getLogoUrl());
        ret.setShopName(shop.getName());
        ret.setShopDesc(shop.getDesc());

        // 店铺评分
        SellerIndicatorDTO ind = sellerOperatorAdapter.querySellerIndicator(sellerId);
        if (ind != null) {
            ret.setTotalScore(ind.getEvaluationTotalScore().toString());
            ret.setDescScore(ind.getEvaluationDescScore().toString());
            ret.setServiceScore(ind.getEvaluationServScore().toString());
            ret.setLogistScore(ind.getEvaluationLogisScore().toString());
        }
    }

    private void fillCustInfo(ShopVipVO ret, CustomerDTO customer) {
        ret.setCustName(customer.getUsername());
        ret.setCustNick(customer.getNickname());
        if (Boolean.TRUE.equals(customer.getPhoneIsBind())) {
            ret.setCustPhone(customer.getPhone());
        }
        if (customer.isB2b()) {
            // b2b extend
            Map<String, String> extMap = customerAdapter.queryExtend(customer.getId(), CustomerApplyExtendDTO.EXTEND_TYPE);
            String apply = extMap.get(CustomerApplyExtendDTO.EXTEND_KEY);
            if (StringUtils.isNotBlank(apply)) {
                // see login-front ApplyInfo
                JSONObject applyJson = JSON.parseObject(apply);
                String companyName = applyJson.getString("companyName");
                ret.setCustCompanyName(companyName);
            }
        }
    }

    @Override
    public void joinShopVip(ShopVipJoinCommand join) {
        CheckContext c = new CheckContext();
        ShopVipVO check = checkJoin(join.getCustId(), join.getSellerId(), c);
        List<String> tags = c.getRelTags();
        Map<String, String> features = c.getRelFeatures();

        // 目前只允许加入一种会员
        if (join.isJoinBVip()) {
            if (check.isBVip()) {
                return; // 已加入, 无需重复加入
            }
            if (!check.isCanJoinBVip()) {
                throw new GmallException(CustomerFrontResponseCode.CANNOT_JOIN_B_VIP);
            }
            tags.add(CustomerSellerRelationTags.B_VIP);
            features.put(CustomerSellerRelationFeatures.KEY_BVIP_JOIN_TIME,
                    String.valueOf(System.currentTimeMillis()));
        } else if (join.isJoinCVip()) {
            if (check.isCVip()) {
                return; // 已加入, 无需重复加入
            }
            if (!check.isCanJoinCVip()) {
                throw new GmallException(CustomerFrontResponseCode.CANNOT_JOIN_C_VIP);
            }
            tags.add(CustomerSellerRelationTags.C_VIP);
            features.put(CustomerSellerRelationFeatures.KEY_CVIP_JOIN_TIME,
                    String.valueOf(System.currentTimeMillis()));
        }

        CustomerSellerRelationCommand req = new CustomerSellerRelationCommand();
        req.setCustId(join.getCustId());
        req.setSellerId(join.getSellerId());
        req.setState(CustomerSellerRelationState.NORMAL.getCode());
        req.setRelTags(tags);
        req.setRelFeatures(features);
        customerAdapter.saveCustomerSellerRel(req);
    }

    private static class CheckContext {
        CustomerSellerRelationDTO rel;
        CustomerDTO cust;

        public List<String> getRelTags() {
            return Optional.ofNullable(rel).map(CustomerSellerRelationDTO::getRelTags)
                    .orElseGet(ArrayList::new);
        }

        public Map<String, String> getRelFeatures() {
            return Optional.ofNullable(rel).map(CustomerSellerRelationDTO::getRelFeatures)
                    .orElseGet(HashMap::new);
        }
    }
}
