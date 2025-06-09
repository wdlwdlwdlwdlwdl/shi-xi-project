package com.aliyun.gts.gmall.center.trade.persistence.repository.impl;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.point.AcBookOrderRecordDO;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.point.AcBookRecordDO;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.point.AcBookRecordParam;
import com.aliyun.gts.gmall.center.trade.persistence.rpc.converter.PointGrantRpcConverter;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.trade.domain.entity.point.PointGrantConfig;
import com.aliyun.gts.gmall.center.trade.domain.entity.point.PointGrantParam;
import com.aliyun.gts.gmall.center.trade.domain.entity.point.PointRollbackExtParam;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.PointGrantRepository;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.*;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookWriteFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.PromotionConfigFacade;
import com.aliyun.gts.gmall.platform.promotion.api.utils.AccountPointUtils;
import com.aliyun.gts.gmall.platform.promotion.common.query.account.AcBookRecordQuery;
import com.aliyun.gts.gmall.platform.promotion.common.type.account.ChangeTypeEnum;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointRollbackParam;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.util.RpcUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PointGrantRepositoryImpl implements PointGrantRepository {
    private final Cache<Integer, PointGrantConfig> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS).build();

    @Autowired
    private PromotionConfigFacade promotionConfigFacade;
    @Autowired
    private AccountBookWriteFacade accountBookWriteFacade;

    @Autowired
    private AccountBookReadFacade accountBookReadFacade;

    @Autowired
    private PointGrantRpcConverter pointGrantRpcConverter;
    @Override
    public PointGrantConfig getGrantConfig() {
        try {
            return cache.get(0, this::getNoCache);
        } catch (ExecutionException e) {
            return ErrorUtils.throwUndeclared(e);
        }
    }

    /**
     * 根据id查询积分赠送记录
     * @param id
     * @return
     */
    public AcBookRecordDO queryGrantRecord(Long id) {
        RpcResponse<AcBookRecordDTO> rpcResponse =accountBookReadFacade.queryRecord(id);
        if (rpcResponse.isSuccess()) {
            if (rpcResponse.getData() == null) {
                return null;
            }
            return pointGrantRpcConverter.toAcBookRecordDO(rpcResponse.getData());
        } else {
            throw new GmallException(CommonErrorCode.CALL_PROMOTION_CENTER_FAIL, rpcResponse.getFail().getCode(), rpcResponse.getFail().getMessage());
        }
    }

    @Override
    public PageInfo<AcBookRecordDO> queryGrantRecord(AcBookRecordParam query) {
        RpcResponse<PageInfo<AcBookRecordDTO>> rpcResponse = accountBookReadFacade.queryRecords(pointGrantRpcConverter.toAcBookRecorQuery(query));
        if (rpcResponse.isSuccess() && rpcResponse.getData() != null) {
            PageInfo<AcBookRecordDO> pageInfo = new PageInfo<>();
            pageInfo.setList(rpcResponse.getData().getList().stream().map(pointGrantRpcConverter::toAcBookRecordDO).collect(Collectors.toList()));;
            pageInfo.setTotal(rpcResponse.getData().getTotal());
            pageInfo.setTabs(rpcResponse.getData().getTabs());
            pageInfo.setTraceId(rpcResponse.getData().getTraceId());
            return pageInfo;
        } else {
            throw new GmallException(CommonErrorCode.CALL_PROMOTION_CENTER_FAIL, rpcResponse.getFail().getCode(), rpcResponse.getFail().getMessage());
        }
    }

    /**
     * 分页查询积分赠送记录
     * @param query
     * @return
     */
    @Override
    public PageInfo<AcBookOrderRecordDO> queryGrantOrderRecord(AcBookRecordParam query) {
        RpcResponse<PageInfo<AcBookOrderRecordDTO>> rpcResponse = accountBookReadFacade.queryBookOrderRecords(pointGrantRpcConverter.toAcBookOrderRecorQuery(query));
        if (rpcResponse.isSuccess() && rpcResponse.getData() != null) {
            PageInfo<AcBookOrderRecordDO> pageInfo = new PageInfo<>();
            pageInfo.setList(rpcResponse.getData().getList().stream().map(pointGrantRpcConverter::toAcBookOrderRecordDO).collect(Collectors.toList()));;
            pageInfo.setTotal(rpcResponse.getData().getTotal());
            pageInfo.setTabs(rpcResponse.getData().getTabs());
            pageInfo.setTraceId(rpcResponse.getData().getTraceId());
            return pageInfo;
        } else {
            throw new GmallException(CommonErrorCode.CALL_PROMOTION_CENTER_FAIL, rpcResponse.getFail().getCode(), rpcResponse.getFail().getMessage());
        }
    }

    /**
     * 根据赠送记录的保留状态
     * @param id
     * @param oldReserveState
     * @return
     */
    @Override
    public boolean updateGrantRecordReserveState(Long id, Integer oldReserveState, Integer newReserveState) {
        RpcResponse<Boolean> rpcResponse = accountBookWriteFacade.updateGrantRecordReserveState(id, oldReserveState, newReserveState);
        if (rpcResponse.isSuccess() && rpcResponse.getData() != null) {
            return rpcResponse.getData();
        } else {
            throw new GmallException(CommonErrorCode.CALL_PROMOTION_CENTER_FAIL, rpcResponse.getFail().getCode(), rpcResponse.getFail().getMessage());
        }
    }

    @Override
    public Date calcInvalidDate(PointGrantConfig config, Date startDate) {
        AcPointConfigDTO c = new AcPointConfigDTO();
        c.setInvalidType(config.getInvalidType());
        c.setInvalidYear(config.getInvalidYear());
        c.setInvalidMonth(config.getInvalidMonth());
        return c.generateInvalidDate(startDate);
    }

    private PointGrantConfig getNoCache() {
        RpcResponse<AcPointConfigDTO> resp = RpcUtils.invokeRpc(
            () -> promotionConfigFacade.queryAccountPointConfig(),
            "promotionConfigFacade.queryAccountPointConfig",
            I18NMessageUtils.getMessage("query.points.config"),
            null
        );  //# "查积分赠送配置"
        AcPointConfigDTO c = resp.getData();
        // 每1元, 赠送多少展示积分
        BigDecimal displayValue = c.getGrantPointValue();
        // 每1元, 赠送多少原子积分
        Long pointValue = null;
        if (displayValue != null) {
            pointValue = displayValue.multiply(AccountPointUtils.bigUnit).longValue();
        }
        return PointGrantConfig.builder()
            .tradeGrantPoint(c.getTradeGrantPoint())
            .grantPointOneYuan(pointValue)
            .invalidType(c.getInvalidType())
            .invalidYear(c.getInvalidYear())
            .invalidMonth(c.getInvalidMonth())
            .grantPointReserveDay(c.getGrantPointReserveDay())
            .build();
    }

    @Override
    public void grantPoint(PointGrantParam param) {
        ChangeTypeEnum changeType = ChangeTypeEnum.trade_grant;
        AcBookRecordDTO record = new AcBookRecordDTO();
        record.setCustId(param.getCustId());
        record.setChangeAssets(param.getCount());
        record.setChangeType(changeType.getCode());
        record.setChangeName(changeType.getDesc());
        record.setInvalidTime(param.getInvalidDate());
        record.setBizId(String.valueOf(param.getMainOrderId()));
        record.setReserveState(param.getReserveState());
        record.setId(param.getAcBookRecordId());
        record.setEffectTime(param.getEffectTime());
        record.setRemark(param.getRemark());
        RpcUtils.invokeRpc(() ->
            accountBookWriteFacade.grantAssets(record),
            "accountBookWriteFacade.grantAssets",
            I18NMessageUtils.getMessage("points.award"),
            record
        );  //# "积分赠送"
    }

    @Override
    public void rollbackGrantPoint(PointRollbackParam param) {
        ChangeTypeEnum type = ChangeTypeEnum.trade_grant_back_deduct;
        AcBookDeductDTO record = new AcBookDeductDTO();
        record.setCustId(param.getCustId());
        record.setChangeAssets(param.getCount());
        record.setChangeType(type.getCode());
        record.setChangeName(type.getDesc());
        //目标退款的对象
        record.setBizId(String.valueOf(param.getMainReversalId()));
        record.setTargetBizId(String.valueOf(param.getMainOrderId()));
        record.setTargetChangeType(ChangeTypeEnum.trade_grant.getCode());
        RpcUtils.invokeRpc(
            () -> accountBookWriteFacade.grantReturn(record),
            "accountBookWriteFacade.grantReturn",
            I18NMessageUtils.getMessage("return.awarded.points"),
            record
        );  //# "退赠送积分"
    }

    @Override
    public long rollbackGrantPointPositive(PointRollbackExtParam param) {
        ChangeTypeEnum type = ChangeTypeEnum.trade_grant_back_deduct;
        AcGrantReturnDTO record = new AcGrantReturnDTO();
        record.setCustId(param.getCustId());
        record.setChangeAssets(param.getCount());
        record.setChangeType(type.getCode());
        record.setChangeName(type.getDesc());
        record.setMinChangeAssets(param.getMinChangeCount());
        //目标退款的对象
        record.setBizId(String.valueOf(param.getMainReversalId()));
        record.setTargetBizId(String.valueOf(param.getMainOrderId()));
        record.setTargetChangeType(ChangeTypeEnum.trade_grant.getCode());
        RpcResponse<Long> resp = RpcUtils.invokeRpc(
            () -> accountBookWriteFacade.grantReturnPositive(record),
            "accountBookWriteFacade.grantReturn",
            I18NMessageUtils.getMessage("return.awarded.points")+"_Positive",
            record
        );  //# "退赠送积分
        return resp.getData();
    }

}
