package com.aliyun.gts.gmall.platform.trade.core.bizcode;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractRpcRequest;
import com.aliyun.gts.gmall.framework.biz.component.pay.input.PayRefundInput;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IBizEntity;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.BizCodeEnum;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 业务身份模型
 *
 * @author xinchen
 */
@Data
@ApiOperation(value = "业务身份识别类")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizCodeEntity implements IBizEntity {

    /**
     * 订单身份编码
     */
    private String bizCode;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 订单渠道
     */
    private String orderChannel;

    /**
     * 活动类型
     */
    private String promotionType;

    public static BizCodeEntity buildByReq(AbstractRpcRequest req) {
        String bizCode = StringUtils.isBlank(req.getExtendBizIdentity()) ?
            BizCodeEnum.NORMAL_TRADE.getCode() : req.getExtendBizIdentity();
        BizCodeEntity bizCodeEntity = new BizCodeEntity();
        bizCodeEntity.setBizCode(bizCode);
        local.set(bizCodeEntity);
        return bizCodeEntity;
    }

    public static BizCodeEntity buildByCode(String code) {
        BizCodeEntity bizCodeEntity = new BizCodeEntity();
        bizCodeEntity.setBizCode(code);
        local.set(bizCodeEntity);
        return bizCodeEntity;
    }

    public static BizCodeEntity getFromThreadLocal() {
        return local.get();
    }

    final static ThreadLocal<BizCodeEntity> local = new ThreadLocal<>();


    // ========================== 以下 从订单获取bizCode ========================


    public static BizCodeEntity buildWithDefaultBizCode(MainOrder mainOrder) {
        BizCodeEntity bizCodeEntity = BizCodeEntity.builder()
                .bizCode(getDefaultBizCode(mainOrder))
                .orderType(String.valueOf(mainOrder.getOrderType()))
                .orderChannel(mainOrder.getOrderChannel())
                .build();
        local.set(bizCodeEntity);
        return bizCodeEntity;
    }

    /**
     * 返回默认第一个bizcode
     */
    private static String getDefaultBizCode(MainOrder mainOrder) {
        if (CollectionUtils.isEmpty(mainOrder.getBizCodes())) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, I18NMessageUtils.getMessage("order.business.identity.exception")+":" + mainOrder.getPrimaryOrderId());  //# "订单业务身份异常
        }
        return mainOrder.getBizCodes().get(0);
    }

    public static List<BizCodeEntity> getOrderBizCode(MainOrder order) {
        if (CollectionUtils.isEmpty(order.getBizCodes())) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, I18NMessageUtils.getMessage("order.business.identity.exception")+":" + order.getPrimaryOrderId());  //# "订单业务身份异常
        }
        //TODO：map的作用是对流中的每一个元素进行转换，生成一个新的流。
        return order.getBizCodes().stream().map(code -> BizCodeEntity.builder()
                .bizCode(code)
                .orderType(String.valueOf(order.getOrderType()))
                .orderChannel(order.getOrderChannel())
                .build()
        ).collect(Collectors.toList());
    }

    /**
     * 下单时所有订单的bizCode 集合，去重
     */
    public static List<BizCodeEntity> getOrderBizCode(CreatingOrder order) {

        //这边为什么要用flatMap？  原因是：set最终要获取的是List<BizCodeEntity>，而BizCodeEntity中包含bizCode，BizCode包含在两个list中
        //即 List<MainOrder> mainOrders -> List<String> bizCodes , 两层List需要用到flatMap。
        Set<BizCodeEntity> set = order.getMainOrders().stream()
                .flatMap(main -> getOrderBizCode(main).stream())
                .collect(Collectors.toSet());

        return new ArrayList<>(set);
    }

    public static List<BizCodeEntity> getOrderBizCode(TcOrderDO order) {
        if (CollectionUtils.isEmpty(order.getBizCode())) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, I18NMessageUtils.getMessage("order.business.identity.exception")+":" + order.getOrderId());  //# "订单业务身份异常
        }
        return order.getBizCode().stream()
                .map(code -> BizCodeEntity.builder()
                        .bizCode(code)
                        .orderType(String.valueOf(order.getOrderAttr().getOrderType()))
                        .orderChannel(order.getOrderChannel()).build())
                .collect(Collectors.toList());
    }

    public static BizCodeEntity getDefaultOrderBizCode(TcOrderDO order) {
        List<BizCodeEntity> codes = getOrderBizCode(order);
        if (CollectionUtils.isEmpty(codes)) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG,
                    I18NMessageUtils.getMessage("order.business.identity.exception")+":" + order.getOrderId());  //# "订单业务身份异常
        }
        return codes.get(0);
    }
}
