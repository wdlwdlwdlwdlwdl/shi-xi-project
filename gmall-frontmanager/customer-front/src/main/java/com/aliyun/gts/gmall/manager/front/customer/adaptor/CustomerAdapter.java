package com.aliyun.gts.gmall.manager.front.customer.adaptor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.aliyun.gts.gmall.center.user.api.dto.input.NewCustomerQueryReq;
import com.aliyun.gts.gmall.center.user.api.facade.CustomerReadExtFacade;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.customer.converter.CustomerConverter;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CustomerGrowthQuery;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.GrGroupRelationDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.GrBizGroupReadFacade;
import com.aliyun.gts.gmall.platform.promotion.common.query.GrGroupQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.*;
import com.aliyun.gts.gmall.platform.user.api.dto.output.*;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerSellerRelationFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerWriteFacade;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.CUSTOMER_CENTER_ERROR;
import static com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode.*;

/**
 * 会员信息接口
 *
 * @author tiansong
 */
@Slf4j
@ApiOperation(value = "会员信息接口")
@Component
public class CustomerAdapter {
    @Autowired
    private CustomerReadFacade  customerReadFacade;
    @Autowired
    private GrBizGroupReadFacade grBizGroupReadFacade;
    @Autowired
    private CustomerReadExtFacade customerReadExtFacade;
    @Autowired
    private CustomerWriteFacade customerWriteFacade;
    @Autowired
    private CustomerConverter customerConverter;
    @Autowired
    private DatasourceConfig datasourceConfig;
    @Autowired
    private CustomerSellerRelationFacade customerSellerRelationFacade;

    @Resource
    @Qualifier("cacheManager")
    private CacheManager cacheManager;

    @Value("${front-manager.cust.cacheSeconds:180}")
    private Long custCacheSeconds;

    private static final String CUST_CACHE_PREFIX = "custIdCache_";

    private final DubboBuilder builder = DubboBuilder.builder().logger(log).sysCode(CUSTOMER_CENTER_ERROR).build();

    /**
     * 校验手机号和密码【强依赖】
     *
     * @param custId 手机号
     * @param pwd    密码
     * @return 是否匹配，true：匹配；exception：不匹配
     */
    public Boolean checkPwdById(Long custId, String pwd) {
        CustomerPwdCheckRpcRequest customerPwdCheckRpcRequest = new CustomerPwdCheckRpcRequest();
        customerPwdCheckRpcRequest.setPwd(pwd);
        customerPwdCheckRpcRequest.setCustId(custId);
        return builder.create(datasourceConfig)
            .id(DsIdConst.customer_base_checkPwd)
            .queryFunc((Function<CustomerPwdCheckRpcRequest, RpcResponse<Boolean>>) request -> {
                RpcResponse<Boolean> rpcResponse = customerReadFacade.checkByPwd(request);
                if (rpcResponse.isSuccess() && rpcResponse.getData()) {
                    return RpcResponse.ok(Boolean.TRUE);
                }
                return rpcResponse.isSuccess() ? RpcResponse.fail(PASSWORD_IS_WRONG) : RpcResponse.fail(rpcResponse.getFail());
            })
            .bizCode(PASSWORD_IS_WRONG)
            .query(customerPwdCheckRpcRequest);
    }

    /**
     * 更新用户信息【强依赖】
     *
     * @param customerWriteCommand 用户信息
     * @return 是否成功，true：是；false：否
     */
    public Boolean update(UpdateCustomerCommand customerWriteCommand) {
        return builder
            .create(datasourceConfig)
            .id(DsIdConst.customer_base_update)
            .queryFunc((Function<UpdateCustomerCommand, RpcResponse<Boolean>>) request -> {
                RpcResponse<CustomerDTO> rpcResponse = customerWriteFacade.update(request);
                if (rpcResponse.isSuccess() && rpcResponse.getData() != null) {
                    return RpcResponse.ok(Boolean.TRUE);
                }
                return RpcResponse.fail(rpcResponse.getFail());
            })
            .bizCode(CUSTOMER_UPDATE_FAIL)
            .query(customerWriteCommand);
    }

    /**
     * 获取用户信息【强依赖】
     *
     * @param custId 用户ID
     * @return 用户信息
     */
    public CustomerDTO queryById(Long custId) {
        CustomerByIdQuery customerByByIdQuery = new CustomerByIdQuery();
        customerByByIdQuery.setId(custId);
        customerByByIdQuery.setOption(CustomerQueryOption.builder().build());
        return builder.create(datasourceConfig)
            .id(DsIdConst.customer_base_queryById)
            .queryFunc((Function<CustomerByIdQuery, RpcResponse<CustomerDTO>>)
                request -> customerReadFacade.query(request))
            .bizCode(CUSTOMER_QUERY_FAIL)
            .query(customerByByIdQuery);
    }

    public CustomerDTO queryByIdWithCache(Long custId) {
        String key = CUST_CACHE_PREFIX + custId;
        Object cust = cacheManager.get(key);
        if (cust == null) {
            cust = queryById(custId);
            if (cust == null) {
                cust = "NULL";
            }
            cacheManager.set(key, cust, custCacheSeconds, TimeUnit.SECONDS);
        }
        if (cust instanceof CustomerDTO) {
            return (CustomerDTO) cust;
        }
        return null;
    }

    /**
     * 获取会员等级信息
     *
     * @param custId
     * @return
     */
    public CustomerLevelDTO queryLevel(Long custId) {
        CustomerByIdQuery customerByIdQuery = CustomerByIdQuery.of(custId, null);
        return builder.create(datasourceConfig)
            .id(DsIdConst.customer_base_queryLevel)
            .queryFunc((Function<CustomerByIdQuery, RpcResponse<CustomerLevelDTO>>) request -> customerReadFacade.queryCustomerLevel(request))
            .bizCode(CUSTOMER_LEVEL_FAIL)
            .query(customerByIdQuery);
    }

    /**
     * 获取会员等级配置信息
     *
     * @return
     */
    public List<CustomerLevelConfigDTO> queryLevelConfig() {
        return builder.create(datasourceConfig)
            .id(DsIdConst.customer_base_queryLevelConfig)
            .queryFunc((Function<DummyQuery, RpcResponse<List<CustomerLevelConfigDTO>>>) request -> customerReadFacade.queryCustomerLevelConfig(request))
            .bizCode(CUSTOMER_LEVEL_CONFIG_FAIL)
            .query(DummyQuery.INSTANCE);
    }

    /**
     * 判断用户是否是days天内的新用户
     * @param custId
     * @param days
     * @return
     */
    public Boolean isNewCustomer(Long custId, int days) {
        NewCustomerQueryReq req = new NewCustomerQueryReq();
        req.setId(custId);
        req.setLimit(days);
        return builder.create(datasourceConfig)
            .id(DsIdConst.customer_new_predict)
            .queryFunc((Function<NewCustomerQueryReq, RpcResponse<Boolean>>) request -> customerReadExtFacade.queryIsNewCustomer(request))
            .strong(false)
            .bizCode(CUSTOMER_NEW_PREDICT_FAIL)
            .query(req, false);
    }

    public GrGroupRelationDTO queryGrGroupRelation(List<Long> groupIds, Long custId) {
        GrGroupQuery q = new GrGroupQuery();
        q.setPage(new PageParam(PageParam.DEFAULT_PAGE_NO,PageParam.MIN_PAGE_SIZE));
        q.setGroupIds(groupIds);
        q.setDomainId(custId);
        q.setType(2);
        PageInfo<GrGroupRelationDTO> relations =  builder.create(datasourceConfig)
            .id(DsIdConst.customer_group_relation)
            .queryFunc((Function<GrGroupQuery, RpcResponse<PageInfo<GrGroupRelationDTO>>>) req -> grBizGroupReadFacade.queryRelation(req))
            .strong(false)
            .bizCode(CUSTOMER_NEW_PREDICT_FAIL)
            .query(q);
        if(Objects.isNull(relations) || CollectionUtils.isEmpty(relations.getList())) {
            return null;
        }
        return relations.getList().get(0);
    }

    public CustomerSellerRelationDTO queryCustomerSellerRel(Long custId, Long sellerId) {
        CustomerSellerRelationUk req = new CustomerSellerRelationUk();
        req.setCustId(custId);
        req.setSellerId(sellerId);
        return builder.create(datasourceConfig)
            .id(DsIdConst.customer_seller_relation)
            .queryFunc((Function<CustomerSellerRelationUk, ?>) customerSellerRelationFacade::queryRelation)
            .bizCode(CUSTOMER_QUERY_FAIL)
            .query(req);
    }

    public void saveCustomerSellerRel(CustomerSellerRelationCommand req) {
        Integer up = builder.create(datasourceConfig)
            .id(DsIdConst.customer_seller_relation_save)
            .queryFunc((Function<CustomerSellerRelationCommand, ?>) customerSellerRelationFacade::updateState)
            .bizCode(CUSTOMER_UPDATE_FAIL)
            .query(req);
        if (up > 0) {
            return;
        }
        // 没有则新增
        builder.create(datasourceConfig)
            .id(DsIdConst.customer_seller_relation_save)
            .queryFunc((Function<CustomerSellerRelationCommand, ?>) customerSellerRelationFacade::createRelation)
            .bizCode(CUSTOMER_UPDATE_FAIL)
            .query(req);
    }

    public Map<String, String> queryExtend(Long custId, String type) {
        CustomerExtendQuery req = new CustomerExtendQuery();
        req.setCustId(custId);
        req.setType(type);
        PageInfo<CustomerExtendDTO> page = builder.create(datasourceConfig)
            .id(DsIdConst.customer_extend_query)
            .queryFunc((Function<CustomerExtendQuery, ?>) customerReadFacade::queryExtend)
            .bizCode(CUSTOMER_QUERY_FAIL)
            .query(req);
        return Optional.ofNullable(page)
            .map(PageInfo::getList)
            .map(List::stream)
            .map(s -> s.collect(Collectors.toMap(CustomerExtendDTO::getK, CustomerExtendDTO::getV)))
            .orElse(null);
    }

    public PageInfo<CustomerGrowthDTO> queryGrowthRecords(CustomerGrowthQuery customerGrowthQuery) {
        CustGrowthHistoryQuery custGrowthHistoryQuery = new CustGrowthHistoryQuery();
        custGrowthHistoryQuery.setCustId(customerGrowthQuery.getCustId());
        custGrowthHistoryQuery.setType(customerGrowthQuery.getType());
        custGrowthHistoryQuery.setPage(customerGrowthQuery.getPage());
        PageInfo<CustomerGrowthDTO> growthDTOPageInfo = builder.create(datasourceConfig)
            .id(DsIdConst.customer_growth_query)
            .queryFunc((Function<CustGrowthHistoryQuery, ?>) customerReadFacade::queryCustGrowthRecords)
            .bizCode(CUSTOMER_QUERY_FAIL)
            .query(custGrowthHistoryQuery);
        return growthDTOPageInfo;

    }

    public List<CustomerGrowthSumDTO> queryCustomerGrowths(CustomerGrowthQuery customerGrowthQuery) {
        CustGrowthHistoryQuery custGrowthHistoryQuery = new CustGrowthHistoryQuery();
        custGrowthHistoryQuery.setCustId(customerGrowthQuery.getCustId());
        custGrowthHistoryQuery.setType(customerGrowthQuery.getType());
        List<CustomerGrowthSumDTO> sumDTOS = builder.create(datasourceConfig)
            .id(DsIdConst.customer_growth_type_query)
            .queryFunc((Function<CustGrowthHistoryQuery, ?>) customerReadFacade::queryCustomerGrowthDTO)
            .bizCode(CUSTOMER_QUERY_FAIL)
            .query(custGrowthHistoryQuery);
        return sumDTOS;
    }
}