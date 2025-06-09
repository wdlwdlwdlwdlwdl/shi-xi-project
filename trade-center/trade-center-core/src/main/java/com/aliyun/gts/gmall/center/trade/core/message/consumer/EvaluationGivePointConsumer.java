package com.aliyun.gts.gmall.center.trade.core.message.consumer;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.center.trade.api.dto.constants.TradeFrontExtendKeyConstants;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvaluationDTORpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvaluationGivePointReq;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvaluationExtService;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookRecordDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcPointConfigDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookWriteFacade;
import com.aliyun.gts.gmall.platform.promotion.common.type.account.ChangeTypeEnum;
import com.aliyun.gts.gmall.platform.promotion.common.util.JsonUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.dubbo.common.convert.Converter;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author yilj
 */
@Slf4j
@MQConsumer(
    topic = "${trade.evaluation.give.point.topic}",
    groupId = "${trade.evaluation.give.point.groupId}",
    tag = "evaluationpoint"
)
public class EvaluationGivePointConsumer implements ConsumeEventProcessor {

    @Resource
    private AccountBookWriteFacade accountBookWriteFacade;

    @Resource
    private EvaluationExtService evaluationExtService;

    private static final String POINT_CONTROL_ERROR="3030003";

    @Override
    public boolean process(StandardEvent event) {
        EvaluationGivePointReq pointReq=(EvaluationGivePointReq) event.getPayload().getData();
        try {
            return grantIntegral(pointReq);
        } catch (Exception e) {
            log.error("EvaluationGivePointConsumer消费积分消息失败," + JsonUtils.toJSONString(pointReq), e);
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean grantIntegral(EvaluationGivePointReq pointReq) {
        try {
            AcBookRecordDTO recordDTO = new AcBookRecordDTO();
            recordDTO.setCreator(pointReq.getCreator());
            recordDTO.setCreateId(pointReq.getCreateId());
            //需要乘以*1000
            recordDTO.setChangeAssets(pointReq.getEvaluationPointCount() * 1000L);
            // 调用积分赠送
            AcPointConfigDTO c = new AcPointConfigDTO();
            c.setInvalidType(pointReq.getInvalidType());
            c.setInvalidYear(pointReq.getInvalidYear());
            c.setInvalidMonth(pointReq.getInvalidMonth());
            Date date = c.generateInvalidDate(new Date());
            recordDTO.setCustId(pointReq.getCustId());
            recordDTO.setInvalidTime(date);
            recordDTO.setBizId(ChangeTypeEnum.evaluate_grant.name() + "|" + pointReq.getEvaluationId());
            recordDTO.setChangeType(ChangeTypeEnum.evaluate_grant.getCode());
            recordDTO.setChangeName(ChangeTypeEnum.evaluate_grant.getDesc());
            recordDTO.setRemark(I18NMessageUtils.getMessage("review.points.award"));  //# "评价送积分"
            RpcResponse<Boolean> rpcResponse = accountBookWriteFacade.grantAssets(recordDTO);
            log.info("EvaluationGivePointConsumer评价送积分返回:{}", JSON.toJSONString(rpcResponse));
            if (rpcResponse.isSuccess()) {
                EvaluationDTO evaluationDTO=evaluationExtService.getById(pointReq.getEvaluationId());
                EvaluationDTORpcReq evaluationDTORpcReq = new EvaluationDTORpcReq();

                //copy the properties
                BeanUtils.copyProperties(evaluationDTORpcReq, evaluationDTO);

                evaluationDTO.getExtend().put(TradeFrontExtendKeyConstants.EVALUATION_GRANTED, "Y");
                evaluationExtService.updateEvaluation(evaluationDTORpcReq);
                return Boolean.TRUE;
            } else {
                if(POINT_CONTROL_ERROR.equals(rpcResponse.getFail().getCode())){
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
        }catch (Exception e){
            log.error("EvaluationGivePointConsumer保存积分消息失败," + JsonUtils.toJSONString(pointReq), e);
            return Boolean.FALSE;
        }
    }
}
