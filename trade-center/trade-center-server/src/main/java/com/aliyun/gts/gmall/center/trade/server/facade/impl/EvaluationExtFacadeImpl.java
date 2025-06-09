package com.aliyun.gts.gmall.center.trade.server.facade.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.trade.api.dto.constants.TradeFrontExtendKeyConstants;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvaluationDTORpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvaluationGivePointReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.IdRpcReq;
import com.aliyun.gts.gmall.center.trade.api.facade.EvaluationExtFacade;
import com.aliyun.gts.gmall.center.trade.api.util.EvaluationPointUtil;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvaluationExtService;
import com.aliyun.gts.gmall.framework.api.mq.dto.SearchMessage;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.mq.esindex.entity.MessageDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.PromotionConfigDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.PromotionConfigFacade;
import com.aliyun.gts.gmall.platform.promotion.common.constant.ConfigGroups;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionConfigKey;
import com.aliyun.gts.gmall.platform.promotion.common.query.PromConfigQuery;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.EvaluateMessageDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import com.aliyun.gts.gmall.platform.trade.core.convertor.EvaluationConverter;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.MessageSendManager;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerByIdQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerQueryOption;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author : zhang.beilei
 * @date : 2022/10/18 17:47
 **/
@Service
@Slf4j
public class EvaluationExtFacadeImpl implements EvaluationExtFacade {

    @Resource
    EvaluationExtService evaluationExtService;

    @Resource
    private CustomerReadFacade customerReadFacade;

    @Resource
    public AccountBookReadFacade accountBookReadFacade;

    @Resource
    private PromotionConfigFacade configFacade;
    @Resource
    MessageSendManager messageSendManager;

    @Resource
    CacheManager cacheManager;

    private static final Integer AGREE_STATUS_VAL = 2;

    private static final Integer POINT_SWITCH_STATUS_VAL = 1;

    private static final Integer EV_TYPE_VAL = 1;

    private static final Integer RATE_DESC_LENTH = 10;

    private static final String EVALUATIONPOINTSWITCH = "evaluationPointSwitch";

    private static final String EVALUATIONPOINTCOUNT = "evaluationPointCount";

    private static final String EVALUATIONTOTALPOINTCOUNT = "evaluationTotalPointCount";


    //pageSize 最大不超过50 - 可能修改部分
//    private Integer PAGE_SIZE = 50;
    private Integer PAGE_SIZE = 100;


    @Value("${trade.evaluation.give.point.topic}")
    private String EVALUATION_POINT_TOPIC;
    @Value("${elasticsearch.name.tcEvaluation:evaluation_new_dev}")
    private String indexName;
    @Value("${trade.syn.topic:}")
    private String topic;
    @Value("${trade.syn.tag:}")
    private String tag;
    @Value("${trade.order.sellerevaluate.topic:}")
    private String sellerEvaluateTopic;
    @Autowired
    EvaluationConverter evaluationConverter;

    @Override
    public RpcResponse<EvaluationDTO> getById(IdRpcReq idReq) {
        EvaluationDTO evaluationDTO = evaluationExtService.getById(idReq.getId());
        return RpcResponse.ok(evaluationDTO);
    }

    @Override
    public RpcResponse<Boolean> update(EvaluationDTORpcReq evaluationDTO) {
        if(evaluationExtService.updateEvaluation(evaluationDTO)){
            updateEsInfo(evaluationDTO);
            Map extendMap = evaluationDTO.getExtend();
            Integer evaluationApproveStatus = (Integer) extendMap.get(TradeFrontExtendKeyConstants.EVALUATION_APPROVE_STATUS);
            EvaluationDTO evaluation = evaluationExtService.getById(evaluationDTO.getId());
            if(AGREE_STATUS_VAL.equals(evaluationApproveStatus)){
                if(evaluation.getItemId()==null || evaluation.getItemId()==0){
                    sendSellerMsg(evaluationDTO);
                }
            }
        }
        return RpcResponse.ok(null);
    }

    private void sendSellerMsg(EvaluationDTORpcReq evaluationDTO){
        TcEvaluationDO tcEvaluationDO = evaluationConverter.toTcEvaluationDO(evaluationDTO);
        EvaluateMessageDTO dto = new EvaluateMessageDTO();
        BeanUtils.copyProperties(tcEvaluationDO, dto);
        messageSendManager.sendMessage(dto, sellerEvaluateTopic, "SUCCESS");
    }

    @Override
    @Transactional
    public RpcResponse<Boolean> updateEvaluation(EvaluationDTORpcReq evaluationDTO) {
        //获取评论是否审核通过
        Map extendMap = evaluationDTO.getExtend();
        Integer evaluationApproveStatus = (Integer) extendMap.get(TradeFrontExtendKeyConstants.EVALUATION_APPROVE_STATUS);
        Integer evType = (Integer) extendMap.get("evType");
        //评论描述
        String rateDesc = evaluationDTO.getRateDesc();
        //评论图片
        List<String> ratePicList = evaluationDTO.getRatePic();
        boolean bool = evaluationExtService.updateEvaluation(evaluationDTO);
        if(bool){
            updateEsInfo(evaluationDTO);
        }
        //审核通过& 追评评论不送积分、参与抽奖活动的奖品不支持评论送积分
        if (AGREE_STATUS_VAL.equals(evaluationApproveStatus) && EV_TYPE_VAL.equals(evType)) {
            //临时取消图片验证送积分
            if (Objects.nonNull(ratePicList) && !ratePicList.isEmpty() && StringUtils.isNotEmpty(rateDesc)) {
                String desc = EvaluationPointUtil.getAvailableCharacter(rateDesc);
                if (desc.length() >= RATE_DESC_LENTH) {
                    //发放总量，超过后活动限制，自动停止
                    givePointByEvaluation(evaluationDTO, extendMap);
                }
            }
            EvaluationDTO evaluation = evaluationExtService.getById(evaluationDTO.getId());
            if(evaluation.getItemId()==null || evaluation.getItemId()==0){
                sendSellerMsg(evaluationDTO);
            }
        }

        return RpcResponse.ok(bool);
    }

    /**
     * 功能描述  赠送积分
     *
     * @param
     * @return
     * @author yilj
     * @date 2022/12/21
     */
    private void givePointByEvaluation(EvaluationDTORpcReq evaluationDTO, Map extendMap) {
        //获取积分规则
        PromConfigQuery query = new PromConfigQuery();
        query.setConfigGroup(ConfigGroups.ACCOUNT_GROUP);
        query.setKey(PromotionConfigKey.ACCOUNT_GLOBAL_CONFIG);
        RpcResponse<PromotionConfigDTO> response = configFacade.queryByKey(query);
        if (response.isSuccess()) {
            PromotionConfigDTO configDTO = response.getData();
            if (configDTO == null) {
                log.warn("积分配置信息为空");
                return;
            }
            JSONObject body = configDTO.getBody();
            if (body == null) {
                log.warn("积分配置信息为空");
                return;
            }
            //是否开启评价送积分
            Integer evaluationPointSwitch = body.getInteger(EVALUATIONPOINTSWITCH);
            //送积分数量
            Integer evaluationPointCount = body.getInteger(EVALUATIONPOINTCOUNT);
            //可以赠送的总积分
            Integer evaluationTotalPointCount = body.getInteger(EVALUATIONTOTALPOINTCOUNT);

            Integer invalidType = body.getInteger("invalidType");
            Integer invalidYear = body.getInteger("invalidYear");
            Integer invalidMonth = body.getInteger("invalidMonth");
            //开启评价送积分
            if (POINT_SWITCH_STATUS_VAL.equals(evaluationPointSwitch)) {
                    Long custId = evaluationDTO.getCustId();
                    CustomerQueryOption queryOption = new CustomerQueryOption();
                    RpcResponse<CustomerDTO> customer = customerReadFacade.query(CustomerByIdQuery.of(custId, queryOption));
                    if (customer.isSuccess() && customer.getData() != null) {
                        EvaluationGivePointReq evaluationGivePointReq=new EvaluationGivePointReq();
                        evaluationGivePointReq.setCreateId((Long) extendMap.get(TradeFrontExtendKeyConstants.EVALUATION_APPROVE_USER_ID));
                        evaluationGivePointReq.setCreator((String) extendMap.get(TradeFrontExtendKeyConstants.EVALUATION_APPROVE_USER_NAME));
                        evaluationGivePointReq.setCustId(evaluationDTO.getCustId());
                        evaluationGivePointReq.setEvaluationPointCount(evaluationPointCount);
                        evaluationGivePointReq.setEvaluationId(evaluationDTO.getId());
                        evaluationGivePointReq.setInvalidType(invalidType);
                        evaluationGivePointReq.setInvalidYear(invalidYear);
                        evaluationGivePointReq.setInvalidMonth(invalidMonth);
                        messageSendManager.sendMessage(evaluationGivePointReq,EVALUATION_POINT_TOPIC,"evaluationpoint");
                    }
            }
        }
    }


    private void updateEsInfo(EvaluationDTORpcReq tcEvaluationDO) {
        SearchMessage message = new SearchMessage();
        //同步ES
        JSONObject param = new JSONObject();
        param.put("id", tcEvaluationDO.getId());
        param.put("evaluation_approve_status", tcEvaluationDO.getExtend().get("evaluationApproveStatus"));
        message.setContent(param);
        message.setType(2);
        message.setIndexName(indexName);
        message.setId(String.valueOf(tcEvaluationDO.getId()));
        MessageDTO msg = new MessageDTO();
        msg.setMessage(message);
        messageSendManager.sendMessage(message, topic, tag);
    }
}
