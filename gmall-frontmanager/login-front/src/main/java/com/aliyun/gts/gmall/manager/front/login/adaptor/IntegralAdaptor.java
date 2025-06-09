package com.aliyun.gts.gmall.manager.front.login.adaptor;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.user.api.enums.CheckReceivePointTypeEnum;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.GrantIntegralConfig;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookRecordDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcPointConfigDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.GrBizGroupOptReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.PromotionConfigDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookWriteFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.GrBizGroupWriteFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.PromotionConfigFacade;
import com.aliyun.gts.gmall.platform.promotion.common.constant.ConfigGroups;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionConfigKey;
import com.aliyun.gts.gmall.platform.promotion.common.query.PromConfigQuery;
import com.aliyun.gts.gmall.platform.promotion.common.type.BizDomainType;
import com.aliyun.gts.gmall.platform.promotion.common.type.account.ChangeTypeEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.json.JsonObject;
import java.util.*;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月27日 17:30:00
 */
@Slf4j
@Service
public class IntegralAdaptor {


    @Autowired
    PromotionConfigFacade promotionConfigFacade;

    @Autowired
    AccountBookWriteFacade accountBookWriteFacade;

    @Autowired
    AccountBookReadFacade accountBookReadFacade;

    @Resource
    private GrBizGroupWriteFacade grBizGroupWriteFacade;


    @Value("${user.register.promotion.groupid:249}")
    private Long userRegisterPromotionGroupId;





    public PromotionConfigDTO grantIntegral() {
        //查询积分配置信息
        PromConfigQuery query = new PromConfigQuery();
        query.setConfigGroup(ConfigGroups.ACCOUNT_GROUP);
        query.setKey(PromotionConfigKey.ACCOUNT_GLOBAL_CONFIG);
        query.setPage(new PageParam());
        RpcResponse<PromotionConfigDTO> response = promotionConfigFacade.queryByKey(query);
        if (!response.isSuccess()) {
            return null;
        }
        return response.getData();
    }


    public Date calcInvalidDate(GrantIntegralConfig config, Date startDate) {
        AcPointConfigDTO c = new AcPointConfigDTO();
        c.setInvalidType(config.getInvalidType());
        c.setInvalidYear(config.getInvalidYear());
        c.setInvalidMonth(config.getInvalidMonth());
        return c.generateInvalidDate(startDate);
    }


    public Boolean grantAssets(Long customerId,Long registerPointValue,GrantIntegralConfig grantIntegralConfig,Integer checkReceivePointType,String bizId){
        //给用户下发积分
        AcBookRecordDTO acBookRecordDTO = new AcBookRecordDTO();
        acBookRecordDTO.setCustId(customerId);
        acBookRecordDTO.setInvalidTime(calcInvalidDate(grantIntegralConfig,new Date()));
        //需要乘以*1000
        acBookRecordDTO.setChangeAssets(registerPointValue * 1000L);
        acBookRecordDTO.setBizId(bizId);
        acBookRecordDTO.setRemark(I18NMessageUtils.getMessage("new.user.registration")+"/"+I18NMessageUtils.getMessage("auth.autologin.points"));  //# "新用户注册/授权免登发放积分"
        //这里的积分类型能否直接用赠送积分
        if(checkReceivePointType.equals(CheckReceivePointTypeEnum.REGISTER.getCode())){
            acBookRecordDTO.setChangeType(ChangeTypeEnum.new_register_grant.getCode());
            acBookRecordDTO.setChangeName(ChangeTypeEnum.new_register_grant.getDesc());
        }
        else if (checkReceivePointType.equals(CheckReceivePointTypeEnum.NOLOGIN.getCode())){
            acBookRecordDTO.setChangeType(ChangeTypeEnum.no_login_donate_grant.getCode());
            acBookRecordDTO.setChangeName(ChangeTypeEnum.no_login_donate_grant.getDesc());
        }
        RpcResponse<Boolean> booleanRpcResponse = accountBookWriteFacade.grantAssets(acBookRecordDTO);
        if(booleanRpcResponse.isSuccess()){
            return booleanRpcResponse.getData();
        }
        return false;
    }



    /**
     * 创建新用户默认分组信息
     * @param customerDTO
     */
    public void addNewRegisterRelation(CustomerDTO customerDTO) {
        Map<Long, Object> domainPrimaryMap = new HashMap<>();
        domainPrimaryMap.put(customerDTO.getId(), customerDTO.getIin());
        List<Long> domainIds = new ArrayList<>();
        domainIds.add(customerDTO.getId());
        GrBizGroupOptReq req = new GrBizGroupOptReq(userRegisterPromotionGroupId, BizDomainType.cust.getCode(), domainIds, domainPrimaryMap);
        try {
            log.info("grBizGroupWriteFacade.addRelation:{}", JSONObject.toJSONString(req));
            grBizGroupWriteFacade.addRelation(req);
        } catch (Exception e) {
            log.info("addNewRegisterRelation:error" + e.getMessage(), e);
        }
    }
}
