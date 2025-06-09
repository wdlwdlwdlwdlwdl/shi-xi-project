package com.aliyun.gts.gmall.manager.front.customer.facade.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.center.user.api.dto.constants.CustomerConstant;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.framework.i18n.MultiLangText;
import com.aliyun.gts.gmall.framework.tokenservice.service.TokenService;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginGroupRestQuery;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.AddressAdapter;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.CustomerAdapter;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.PromotionAdapter;
import com.aliyun.gts.gmall.manager.front.customer.converter.CustomerConverter;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditCustomerInfoCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditCustomerLanguageCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditHeadUrlCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditPasswordCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CustomerGrowthQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.*;
import com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.customer.facade.CustomerFacade;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.GrGroupRelationDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.CouponInstanceDTO;
import com.aliyun.gts.gmall.platform.promotion.common.util.DateUtils;
import com.aliyun.gts.gmall.platform.user.api.dto.output.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testng.collections.Lists;
import org.testng.collections.Sets;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户信息操作
 *
 * @author tiansong
 */
@Service
public class CustomerFacadeImpl implements CustomerFacade {
    @Autowired
    private CustomerAdapter   customerAdapter;
    @Autowired
    private PromotionAdapter  promotionAdapter;
    @Autowired
    private CustomerConverter customerConverter;

    @Autowired
    protected MultiLangConverter multiLangConverter;

    @Autowired
    private AddressAdapter addressAdapter;


    @Resource
    private TokenService tokenService;

    @Resource
    @Qualifier("cacheManager")
    private CacheManager cacheManager;

    @NacosValue(value = "${front-manager.promotion.newuser.group.id:100}", autoRefreshed = true)
    private Long SPECIFIC_GROUP_ID;

    @NacosValue(value = "${front-manager.promotion.newuser.limit.day:30}", autoRefreshed = true)
    private Integer limit;

    @Value("${front-manager.default-icon}")
    private String defaultIcon;

    @Override
    public Boolean editPassword(EditPasswordCommand editPasswordCommand) {
        // 1. 查询会员信息
        CustomerDTO customerDTO = customerAdapter.queryById(editPasswordCommand.getCustId());
        if (customerDTO == null) {
            throw new FrontManagerException(CustomerFrontResponseCode.CUSTOMER_QUERY_FAIL);
        }
        // 2. 校验旧密码
        if (BooleanUtils.isTrue(customerDTO.getPwdYn())) {
            customerAdapter.checkPwdById(editPasswordCommand.getCustId(), editPasswordCommand.getOldPwd());
        }
        // 3. 更新密码
        return customerAdapter.update(customerConverter.convertPwd(editPasswordCommand));
    }

    @Override
    public Boolean editHeadUrl(EditHeadUrlCommand editHeadUrlCommand) {
        return customerAdapter.update(customerConverter.convertHeadUrl(editHeadUrlCommand));
    }

    @Override
    public Boolean editBaseInfo(EditCustomerInfoCommand editCustomerInfoCommand) {
        return customerAdapter.update(customerConverter.convertBaseInfo(editCustomerInfoCommand));
    }

    /**
     * 编辑用户语言信息
     *
     * @param editCustomerLanguageCommand
     * @return
     */
    @Override
    public RestResponse<String> editLanguageInfo(EditCustomerLanguageCommand editCustomerLanguageCommand) {
        // 相同  不修改
        CustDTO user = UserHolder.getUser();
        CustomerDTO customerDTO = customerAdapter.queryById(editCustomerLanguageCommand.getCustId());
        if(customerDTO == null) {
            // 修改失败提示信息
            return RestResponse.fail("1001", I18NMessageUtils.getMessage("user.not.exist"));
        }
        if (editCustomerLanguageCommand.getLanguage().equals(customerDTO.getLanguage())) {
            return RestResponse.ok(editCustomerLanguageCommand.getLanguage());
        }
        // 修改语言
        Boolean updateFlag = customerAdapter.update(customerConverter.convertLanguageInfo(editCustomerLanguageCommand));
        if (Boolean.TRUE.equals(updateFlag)) {
            return RestResponse.ok(editCustomerLanguageCommand.getLanguage());
        }
        // 修改失败提示信息
        return RestResponse.fail("1001", I18NMessageUtils.getMessage("update.language.fail"));
    }

    @Override
    public CustomerVO queryById(LoginRestQuery loginRestQuery) {
        CustomerDTO customerDTO = customerAdapter.queryById(loginRestQuery.getCustId());
        CustomerLevelDTO customerLevelDTO = customerAdapter.queryLevel(loginRestQuery.getCustId());
        CustomerVO customerVO = customerConverter.convertCustomer(customerDTO, customerLevelDTO);
        // 计算是否是新用户
        if (Objects.nonNull(customerDTO) && Objects.nonNull(customerDTO.getGmtCreate())) {
            Date limitDate = DateUtils.add(new Date(), -limit);
            customerVO.setIsNewUser(customerDTO.getGmtCreate().compareTo(limitDate) > 0);
        }
        //设置默认地址
        List<CustomerAddressDTO> customerAddressDTOList = addressAdapter.queryList(loginRestQuery.getCustId(), Boolean.TRUE);
        if (CollectionUtils.isNotEmpty(customerAddressDTOList)) {
            CustomerAddressDTO customerAddressDTO = customerAddressDTOList.get(0);
            customerVO.setDefaultAddress(customerAddressDTO);
        }
        return customerVO;
    }

    @Override
    public List<CustomerLevelVO> queryLevelList(LoginRestQuery loginRestQuery) {
        // 1. 获取用户等级列表
        List<CustomerLevelConfigDTO> customerLevelConfigDTOList = customerAdapter.queryLevelConfig();
        if (CollectionUtils.isNotEmpty(customerLevelConfigDTOList)) {
            for (CustomerLevelConfigDTO customerLevelConfigDTO : customerLevelConfigDTOList) {
                MultiLangText nameI18n = customerLevelConfigDTO.getNameI18n();
                if (null != nameI18n) {
                    customerLevelConfigDTO.setName(multiLangConverter.mText_to_str(customerLevelConfigDTO.getNameI18n()));
                }
            }
        }

        List<CustomerLevelVO> customerLevelVOList = customerConverter.convertLevelList(customerLevelConfigDTOList);
        // 2. 获取登录用户等级信息
        CustomerLevelDTO customerLevelDTO = customerAdapter.queryLevel(loginRestQuery.getCustId());
        this.fillCurrentLevel(customerLevelVOList, customerLevelDTO);
        // 3. 获取用户权益信息
        Set<Long> couponIdSet = customerLevelConfigDTOList.stream().map(CustomerLevelConfigDTO::getAwards)
                .filter(Objects::nonNull).flatMap(v -> Optional.ofNullable(v.getCoupons())
                        .orElse(Collections.emptyList()).stream()).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(couponIdSet)) {
            // 3.1 获取所有优惠券信息
            List<CouponInstanceDTO> couponList = promotionAdapter.queryCouponByIds(couponIdSet);
            List<CustomerPromotionVO> promotionVOList = customerConverter.convertLevelPromotion(couponList);
            // 3.2 用户已经领取的优惠券
            PageInfo<CouponInstanceDTO> pageInfo = promotionAdapter.queryCustomerCoupon(loginRestQuery.getCustId(),
                    couponIdSet);
            this.fillCustomerPromotion(customerLevelVOList, promotionVOList, customerLevelDTO, pageInfo);
        }
        return customerLevelVOList;
    }

    /**
     * 填充会员营销信息
     * @param customerLevelVOList
     * @param promotionVOList
     * @param customerLevelDTO
     * @param pageInfo
     */
    private void fillCustomerPromotion(
        List<CustomerLevelVO> customerLevelVOList,
        List<CustomerPromotionVO> promotionVOList,
        CustomerLevelDTO customerLevelDTO, PageInfo<CouponInstanceDTO> pageInfo) {

        if (CollectionUtils.isEmpty(customerLevelVOList) || CollectionUtils.isEmpty(promotionVOList)) {
            // 未配置优惠券信息或系统出错
            return;
        }
        // 用户当前等级可领取的优惠券ID
        Set<Long> applyCouponIds = (customerLevelDTO == null || customerLevelDTO.getAwards() == null ||
            CollectionUtils.isEmpty(customerLevelDTO.getAwards().getCoupons())) ?
            Sets.newHashSet() : Sets.newHashSet(customerLevelDTO.getAwards().getCoupons());
        // 用户已领取的优惠券ID
        Set<Long> receivedCouponIds = (pageInfo == null || pageInfo.isEmpty()) ? Sets.newHashSet() :
                (pageInfo.getList().stream().map(CouponInstanceDTO::getCampaignId).collect(Collectors.toSet()));

        promotionVOList.stream().forEach(customerPromotionVO -> {
            // 标识用户当前等级可领取的优惠券和已领取的优惠券
            customerPromotionVO.setCanApply(applyCouponIds.contains(customerPromotionVO.getCampaignId()));
            customerPromotionVO.setReceived(receivedCouponIds.contains(customerPromotionVO.getCampaignId()));
        });

        // list to map
        Map<Long, CustomerPromotionVO> promotionVOMap = promotionVOList
            .stream()
            .collect(Collectors.toMap(CustomerPromotionVO::getCampaignId, v -> v, (k1, k2) -> k1));
        customerLevelVOList.forEach(customerLevelVO -> {
            if (CollectionUtils.isEmpty(customerLevelVO.getCoupons())) {
                return;
            }
            // 设置等级优惠券
            List<CustomerPromotionVO> levelPromotionVOList = Lists.newArrayList();
            customerLevelVO.getCoupons().forEach(id -> {
                if (promotionVOMap.get(id) == null) {
                    return;
                }
                levelPromotionVOList.add(promotionVOMap.get(id));
            });
            customerLevelVO.setCustomerPromotionVO(levelPromotionVOList);
        });
    }

    /**
     * 填充等级
     * @param customerLevelVOList
     * @param customerLevelDTO
     */
    private void fillCurrentLevel(List<CustomerLevelVO> customerLevelVOList, CustomerLevelDTO customerLevelDTO) {
        if (CollectionUtils.isEmpty(customerLevelVOList) || customerLevelDTO == null
                || customerLevelDTO.getLevel() == null) {
            return;
        }
        customerLevelVOList.forEach(customerLevelVO -> {
            if (!customerLevelDTO.getLevel().equals(customerLevelVO.getLevel())) {
                return;
            }
            customerLevelVO.setCurrentLevel(Boolean.TRUE);
            customerLevelVO.setCustGrowthSum(customerLevelDTO.getCustGrowthSum());
            customerLevelVO.setLevelExpireDate(customerLevelDTO.getLevelExpireDate());
        });
    }

    @Override
    public NewCustomerVO isNewCust(LoginGroupRestQuery loginGroupRestQuery) {
        // cust 为 null，返回true
        CustDTO custDTO = UserHolder.getUser();
        if (Objects.isNull(custDTO)) {
            return new NewCustomerVO(Boolean.TRUE, Boolean.FALSE, null, null, null);
        }
        // 不配置特定人群分组id的时候，在商城首页，所有人都应该可以看到新人banner图
        if (Objects.isNull(loginGroupRestQuery.getGroupIds())) {
            return new NewCustomerVO(Boolean.TRUE, Boolean.TRUE, custDTO.getCustId(), custDTO.getPhone(), custDTO.getCustPrimary());
        }
        List<Long> filterGroupIds = loginGroupRestQuery.getGroupIds()
            .stream()
            .filter(StringUtils::isNumeric)
            .map(Long::valueOf)
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterGroupIds)) {
            return new NewCustomerVO(Boolean.TRUE, Boolean.TRUE, custDTO.getCustId(), custDTO.getPhone(), custDTO.getCustPrimary());
        }
        // 限制分组数量
        if (filterGroupIds.size() > 10) {
            return new NewCustomerVO(Boolean.TRUE, Boolean.TRUE, custDTO.getCustId(), custDTO.getPhone(), custDTO.getCustPrimary());
        }
        // 判断是否属于新人分组申请
        try {
            if (filterGroupIds.contains(SPECIFIC_GROUP_ID)) {
                return new NewCustomerVO(
                    customerAdapter.isNewCustomer(custDTO.getCustId(), limit),
                    Boolean.TRUE,
                    custDTO.getCustId(),
                    custDTO.getPhone(),
                    custDTO.getCustPrimary()
                );
            } else {
                // 判断当前用户是否在 filterGroupIds 分组里面
                return handleUnspecifiedGroup(filterGroupIds, custDTO.getCustId(), custDTO.getPhone(), custDTO.getCustPrimary());
            }
        } catch (Throwable e) {
            return new NewCustomerVO(Boolean.FALSE, Boolean.TRUE, custDTO.getCustId(), custDTO.getPhone(), custDTO.getCustPrimary());
        }
    }

    /**
     *  判断当前用户是否在 filterGroupIds 分组里面
     * @param groupIds
     * @param custId
     * @param phone
     * @param custPrimary
     * @return
     */
    private NewCustomerVO handleUnspecifiedGroup(List<Long> groupIds, Long custId, String phone, String custPrimary) {
        List<Tuple2<Long, Boolean>> cacheMapList = Lists.newArrayList();
        for (Long groupId : groupIds) {
            Boolean isInGroup = cacheManager.get(CustomerConstant.GROUP_CUST_PREFIX + custId + groupId);
            if (Objects.nonNull(isInGroup)) {
                cacheMapList.add(Tuples.of(groupId, isInGroup));
            }
        }
        Boolean visible = cacheMapList.stream().map(Tuple2::getT2).filter(it -> it.equals(Boolean.TRUE)).findFirst().orElse(Boolean.FALSE);
        if (visible) {
            return new NewCustomerVO(Boolean.TRUE, Boolean.TRUE, custId, phone, custPrimary);
        }
        if (cacheMapList.size() != groupIds.size()) {
            // 过滤 groupIds
            List<Long> cacheGroupIdList = cacheMapList.stream().map(Tuple2::getT1).collect(Collectors.toList());
            List<Long> filterGroupIds = groupIds.stream().filter(it -> !cacheGroupIdList.contains(it)).collect(Collectors.toList());
            GrGroupRelationDTO grGroupRelationDTO = customerAdapter.queryGrGroupRelation(filterGroupIds, custId);
            if (Objects.nonNull(grGroupRelationDTO)) {
                cacheManager.set(CustomerConstant.GROUP_CUST_PREFIX + custId + grGroupRelationDTO.getGroupId(), Boolean.TRUE, 600, TimeUnit.SECONDS);
                return new NewCustomerVO(Boolean.TRUE, Boolean.TRUE, custId, phone, custPrimary);
            } else {
                for (Long groupId : filterGroupIds) {
                    cacheManager.set(CustomerConstant.GROUP_CUST_PREFIX + custId + groupId, Boolean.FALSE, 600, TimeUnit.SECONDS);
                }
            }
        }
        return new NewCustomerVO(Boolean.FALSE, Boolean.TRUE, custId, phone, custPrimary);
    }

    @Override
    public Object testRedis() {
        cacheManager.set("redis-test", Boolean.FALSE, 600, TimeUnit.SECONDS);
        Object o = cacheManager.get("redis-test");
        return o;
    }

    @Override
    public PageInfo<CustomerGrowthRecordVO> queryGrowthRecords(CustomerGrowthQuery customerGrowthQuery) {
        PageInfo<CustomerGrowthDTO> pageInfo = customerAdapter.queryGrowthRecords(customerGrowthQuery);
        if (null != pageInfo) {
            PageInfo<CustomerGrowthRecordVO> result = new PageInfo<>();
            result.setTotal(pageInfo.getTotal());
            result.setList(pageInfo.getList().stream().map(customerConverter::convertCustomerGrowth).collect(Collectors.toList()));
            return result;
        }
        return PageInfo.empty();
    }

    @Override
    public List<CustomerGrowthVO> queryCustomerGrowths(CustomerGrowthQuery customerGrowthQuery) {
        List<CustomerGrowthSumDTO> sumDTOList = customerAdapter.queryCustomerGrowths(customerGrowthQuery);
        if (CollectionUtils.isNotEmpty(sumDTOList)) {
            List<CustomerGrowthVO> customerGrowthVOS = customerConverter.convertCustomerGrowths(sumDTOList);
            return customerGrowthVOS;
        }
        return Collections.EMPTY_LIST;
    }
}