package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookDeductDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookWriteFacade;
import com.aliyun.gts.gmall.platform.promotion.common.query.account.AccountBookQuery;
import com.aliyun.gts.gmall.platform.promotion.common.type.account.ChangeTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.util.PayUtils;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointReduceParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointRollbackParam;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PointRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.util.RpcUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "pointRepository", havingValue = "default", matchIfMissing = true)
public class PointRepositoryImpl implements PointRepository {

    @Autowired
    private AccountBookReadFacade accountBookReadFacade;
    @Autowired
    private AccountBookWriteFacade accountBookWriteFacade;

    @Override
    public Long getUserPoint(Long custId) {
        AccountBookQuery q = new AccountBookQuery();
        q.setCustId(custId);
        RpcResponse<Long> resp = RpcUtils.invokeRpc(
                () -> accountBookReadFacade.queryTotalAssets(q),
                "accountBookReadFacade.queryTotalAssets", I18NMessageUtils.getMessage("points.query"), q);  //# "积分查询"
        return resp.getData();
    }

    @Override
    public void lockPoint(List<PointReduceParam> param) {
        List<AcBookDeductDTO> list = param.stream()
                .map(p -> toAcBookDeductDTO(p))
                .collect(Collectors.toList());
        RpcResponse<Boolean> resp = RpcUtils.invokeRpc(
                () -> accountBookWriteFacade.batchFreeze(list),
                "accountBookWriteFacade.batchFreeze", I18NMessageUtils.getMessage("points.freeze"), list);  //# "积分冻结"
        Assert.isTrue(resp.getData(), "impossible exceptions");
    }

    @Override
    public void unlockPoint(List<PointReduceParam> param) {
        for (PointReduceParam p : param) {
            unlockPoint(p);
        }
    }

    private void unlockPoint(PointReduceParam param) {
        AcBookDeductDTO deduct = toAcBookDeductDTO(param);
        RpcResponse<Boolean> resp = RpcUtils.invokeRpc(
            () -> accountBookWriteFacade.unFreeze(deduct),
            "accountBookWriteFacade.unFreeze",
            I18NMessageUtils.getMessage("points.unfreeze"),
            deduct
        );  //# "积分解冻"
        Assert.isTrue(resp.getData(), "impossible exceptions");
    }

    private static AcBookDeductDTO toAcBookDeductDTO(PointReduceParam param) {
        String bizId = PayUtils.buildOutTradeNo(param.getMainOrderId(), param.getStepNo());
        AcBookDeductDTO record = new AcBookDeductDTO();
        record.setCustId(param.getCustId());
        record.setChangeAssets(param.getCount());
        record.setChangeType(ChangeTypeEnum.trade_deduct.getCode());
        record.setChangeName(ChangeTypeEnum.trade_deduct.getDesc());
        record.setBizId(bizId);
        return record;
    }

    /*
    @Override
    public boolean deductPointsAfterLock(PointReduceParam pointReduceParam) {
        AcBookDeductDTO req = toAcBookDeductDTO(pointReduceParam);
        RpcResponse<Boolean> resp = RpcUtils.invokeRpcNotThrow(
                () -> accountBookWriteFacade.freezeDeduct(req),
                "accountBookWriteFacade.freezeDeduct", I18NMessageUtils.getMessage("points.deduct"), req);  //# "积分扣减"
        return resp != null && resp.isSuccess();
    }

    @Override
    public void rollbackPoint(List<PointRollbackParam> param) {
        for (PointRollbackParam pointRollbackParam : param) {
            AcBookDeductDTO acBookDeductDTO = toAcBookDeductDTO(pointRollbackParam);
            RpcResponse<Boolean> resp = RpcUtils.invokeRpc(
                    () -> accountBookWriteFacade.deductReturn(acBookDeductDTO),
                    "accountBookWriteFacade.deductReturn", I18NMessageUtils.getMessage("points.rollback"), acBookDeductDTO);  //# "积分回滚"
            AssertUtils.isTrue(resp.getData(), "impossible exceptions");
        }
    }

    private AcBookDeductDTO toAcBookDeductDTO(PointRollbackParam param) {
        String targetBizId = PayUtils.buildOutTradeNo(param.getMainOrderId(), param.getStepNo());

        AcBookDeductDTO record = new AcBookDeductDTO();
        record.setBizId(param.getMainReversalId().toString());
        record.setChangeAssets(param.getCount());
        record.setChangeType(ChangeTypeEnum.trade_deduct_back.getCode());
        record.setChangeName(ChangeTypeEnum.trade_deduct_back.getDesc());
        record.setCustId(param.getCustId());
        record.setTargetBizId(targetBizId);
        record.setTargetChangeType(ChangeTypeEnum.trade_deduct.getCode());
        return record;
    }
    */
}
