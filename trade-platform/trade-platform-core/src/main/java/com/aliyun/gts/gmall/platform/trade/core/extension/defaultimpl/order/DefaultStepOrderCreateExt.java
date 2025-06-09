package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.TradeExtendKeyConstants;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmStepExtendDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.PayPriceDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepMeta;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepTemplate;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.GenerateIdService;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.StepOrderCreateExt;
import com.aliyun.gts.gmall.platform.trade.core.util.DivideUtils.Divider;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointExchange;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PointExchangeRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcStepTemplateRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils.getNullZero;

/**
 * 分步骤订单计算 处理基类
 * 如果存在定开 则使用FP4j插件开发
 * 2024-12-11 14:38:01
 */
@Slf4j
@Component
public class DefaultStepOrderCreateExt implements StepOrderCreateExt {
    protected static final String STEP_BASE_PRICE = "STEP_BASE_PRICE";

    @Autowired
    private OrderConverter orderConverter;
    @Autowired
    private GenerateIdService generateIdService;
    @Autowired
    private PointExchangeRepository pointExchangeRepository;
    @Autowired
    private TcStepTemplateRepository tcStepTemplateRepository;

    @Override
    public void fillStepTemplate(MainOrder mainOrder) {
        String name = getTemplateName(mainOrder);
        if (StringUtils.isBlank(name)) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
        StepTemplate template = tcStepTemplateRepository.queryParsedTemplateWithCache(name);
        if (template == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, I18NMessageUtils.getMessage("multi.stage.template.not.exist")+":" + name);  //# 多阶段模版不存在
        }
        mainOrder.setStepTemplate(template);
    }

    @Override
    public void fillStepOrders(MainOrder mainOrder) {
        if (Objects.isNull(mainOrder.getStepTemplate())) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
        Map<Integer, StepOrderPrice> stepPrices = getStepPrices(mainOrder);
        List<StepOrder> stepOrders = new ArrayList<>();
        List<StepMeta> stepMetas = mainOrder.getStepTemplate().getStepMetas();
        int i = 0;
        for (StepMeta step : stepMetas) {
            StepOrderPrice price = stepPrices.get(step.getStepNo());
            if (price == null) {
                throw new GmallException(CommonErrorCode.SERVER_ERROR);
            }
            StepOrder stepOrder = buildStepOrder(step, price, mainOrder);
            stepOrders.add(stepOrder);
            // 分摊阶段价格到子订单
            DivideStepPriceOption opt = new DivideStepPriceOption();
            opt.setAdjFee(false);
            opt.setLastModifiedStep(++i == stepMetas.size());
            divideStepPriceToSubOrder(stepOrder, mainOrder, opt);
        }
        mainOrder.setStepOrders(stepOrders);
        mainOrder.orderAttr().setCurrentStepNo(stepOrders.get(0).getStepNo());
        mainOrder.orderAttr().setCurrentStepStatus(stepOrders.get(0).getStatus());
    }

    /***
     * 计算扩展信息 (firstPay / remainPay)
     *     计算首付和尾款金额
     *     首付 firstPay
     *     尾款 remainPay
     * @param creatingOrder
     * @return
     */
    @Override
    public ConfirmStepExtendDTO calcStepExtend(CreatingOrder creatingOrder) {
        PayPriceDTO firstPrice = new PayPriceDTO();
        PayPriceDTO remainPrice = new PayPriceDTO();
        for (MainOrder mainOrder : creatingOrder.getMainOrders()) {
            if (StepOrderUtils.isMultiStep(mainOrder)) {
                boolean first = true;
                for (StepOrder step : mainOrder.getStepOrders()) {
                    if (first) {
                        add(step.getPrice(), firstPrice);
                        first = false;
                    } else {
                        add(step.getPrice(), remainPrice);
                    }
                }
            } else {
                add(mainOrder.getOrderPrice(), firstPrice);
            }
        }
        // 手笔积分抵扣 -- 积分抵扣 只能用于首付款
        firstPrice.setMaxAvailablePoint(calcMaxAvailablePoint(creatingOrder, firstPrice));
        remainPrice.setMaxAvailablePoint(0L);
        ConfirmStepExtendDTO confirmStepExtendDTO = new ConfirmStepExtendDTO();
        confirmStepExtendDTO.setFirstPay(firstPrice);
        confirmStepExtendDTO.setRemainPay(remainPrice);
        return confirmStepExtendDTO;
    }

    @Override
    public void divideStepPriceToSubOrder(StepOrder step, MainOrder mainOrder, DivideStepPriceOption opt) {
        if (opt.isLastModifiedStep()) {
            reduceLastStepPriceToSubOrder(step, mainOrder, opt);
        } else {
            doDivideStepPriceToSubOrder(step, mainOrder, opt);
        }
    }


    protected void setStepBasePrice(SubOrder subOrder, Integer stepNo, Long stepBasePrice) {
        setStepBasePrice(subOrder.getOrderPrice(), stepNo, stepBasePrice);
    }

    protected void setStepBasePrice(OrderPrice subOrderPrice, Integer stepNo, Long stepBasePrice) {
        Map<Integer, StepOrderPrice> stepAmt = subOrderPrice.getStepAmt();
        if (stepAmt == null) {
            stepAmt = new HashMap<>();
            subOrderPrice.setStepAmt(stepAmt);
        }
        StepOrderPrice stepPrice = stepAmt.get(stepNo);
        if (stepPrice == null) {
            stepPrice = new StepOrderPrice();
            stepAmt.put(stepNo, stepPrice);
        }
        String stepBasePriceText = stepBasePrice == null ? null : stepBasePrice.toString();
        stepPrice.putExtend(STEP_BASE_PRICE, stepBasePriceText);
    }

    protected void doDivideStepPriceToSubOrder(StepOrder step, MainOrder mainOrder, DivideStepPriceOption opt) {
        List<SubPrice> allSubPrice = mainOrder.getAllSubPrice();

        // 此时没有subOrderId, 用index代替
        long[] pointDivFactor = new long[allSubPrice.size()];
        long[] realDivFactor = new long[allSubPrice.size()];
        long pointDivSum = 0L;
        long realDivSum = 0L;
        int idx = 0;
        for (SubPrice subOrderPrice : allSubPrice) {
            Map<Integer, StepOrderPrice> stepAmt = subOrderPrice.getOrderPrice().getStepAmt();
            if (stepAmt == null) {
                stepAmt = new HashMap<>();
                subOrderPrice.getOrderPrice().setStepAmt(stepAmt);
            }
            StepOrderPrice subPrice = stepAmt.get(step.getStepNo());
            if (subPrice == null) {
                subPrice = new StepOrderPrice();
                stepAmt.put(step.getStepNo(), subPrice);
            }
            // 阶段基准金额, 如有则按此金额比例分摊, 如定金金额
            String basePrice = subPrice.getExtend(STEP_BASE_PRICE);
            if (StringUtils.isNotBlank(basePrice)) {
                long basePriceValue = Long.parseLong(basePrice);
                pointDivFactor[idx] = basePriceValue;
                realDivFactor[idx] = basePriceValue;
            } else {
                // 没有基准金额, 则按子订单总金额分摊
                pointDivFactor[idx] = getNullZero(subOrderPrice.getOrderPrice().getPointAmt());
                realDivFactor[idx] = getNullZero(subOrderPrice.getOrderPrice().getOrderRealAmt());
            }
            pointDivSum += pointDivFactor[idx];
            realDivSum += realDivFactor[idx];
            idx++;
        }


        StepOrderPrice stepPrice = step.getPrice();
        PointExchange ex = mainOrder.orderAttr().getPointExchange();
        Divider pointDiv = new Divider(getNullZero(stepPrice.getPointAmt()),
                pointDivSum, allSubPrice.size());
        Divider realDiv = new Divider(getNullZero(stepPrice.getRealAmt()),
                realDivSum, allSubPrice.size());

        idx = 0;
        for (SubPrice subOrderPrice : allSubPrice) {
            long pointAmt = pointDiv.next(pointDivFactor[idx]);
            long realAmt = realDiv.next(realDivFactor[idx]);
            long pointCount = 0;
            if (pointAmt > 0) {
                pointCount = ex.exAmtToCount(pointAmt);
            }
            assertNotNegative(pointAmt, realAmt, pointCount);

            Map<Integer, StepOrderPrice> stepAmt = subOrderPrice.getOrderPrice().getStepAmt();
            StepOrderPrice subPrice = stepAmt.get(step.getStepNo());
            if (opt.isAdjFee()) {
                subPrice.setAdjustPointAmt(getNullZero(subPrice.getAdjustPointAmt()) + (pointAmt - getNullZero(subPrice.getPointAmt())));
                subPrice.setAdjustPointCount(getNullZero(subPrice.getAdjustPointCount()) + (pointCount - getNullZero(subPrice.getPointCount())));
                subPrice.setAdjustRealAmt(getNullZero(subPrice.getAdjustRealAmt()) + (realAmt - getNullZero(subPrice.getRealAmt())));
            }

            subPrice.setPointAmt(pointAmt);
            subPrice.setRealAmt(realAmt);
            subPrice.setPointCount(pointCount);
            idx++;
        }
    }

    protected void assertNotNegative(Long... values) {
        for (Long value : values) {
            if (value != null && value < 0) {
                throw new GmallException(OrderErrorCode.ORDER_PRICE_ILLEGAL);
            }
        }
    }

    protected void reduceLastStepPriceToSubOrder(StepOrder step, MainOrder mainOrder, DivideStepPriceOption opt) {
        for (SubPrice subOrderPrice : mainOrder.getAllSubPrice()) {
            long pointAmt = NumUtils.getNullZero(subOrderPrice.getOrderPrice().getPointAmt());
            long realAmt = NumUtils.getNullZero(subOrderPrice.getOrderPrice().getOrderRealAmt());
            long pointCount = NumUtils.getNullZero(subOrderPrice.getOrderPrice().getPointCount());

            Map<Integer, StepOrderPrice> stepAmt = subOrderPrice.getOrderPrice().getStepAmt();
            if (stepAmt == null) {
                stepAmt = new HashMap<>();
                subOrderPrice.getOrderPrice().setStepAmt(stepAmt);
            }
            for (Entry<Integer, StepOrderPrice> otherStep : stepAmt.entrySet()) {
                if (Objects.equals(otherStep.getKey(), step.getStepNo())) {
                    continue;
                }
                StepOrderPrice otherPrice = otherStep.getValue();
                pointAmt -= NumUtils.getNullZero(otherPrice.getPointAmt());
                realAmt -= NumUtils.getNullZero(otherPrice.getRealAmt());
                pointCount -= NumUtils.getNullZero(otherPrice.getPointCount());
            }
            assertNotNegative(pointAmt, realAmt, pointCount);

            StepOrderPrice subPrice = stepAmt.get(step.getStepNo());
            if (subPrice == null) {
                subPrice = new StepOrderPrice();
                stepAmt.put(step.getStepNo(), subPrice);
            }
            if (opt.isAdjFee()) {
                subPrice.setAdjustPointAmt(getNullZero(subPrice.getAdjustPointAmt()) + (pointAmt - getNullZero(subPrice.getPointAmt())));
                subPrice.setAdjustPointCount(getNullZero(subPrice.getAdjustPointCount()) + (pointCount - getNullZero(subPrice.getPointCount())));
                subPrice.setAdjustRealAmt(getNullZero(subPrice.getAdjustRealAmt()) + (realAmt - getNullZero(subPrice.getRealAmt())));
            }

            subPrice.setPointAmt(pointAmt);
            subPrice.setRealAmt(realAmt);
            subPrice.setPointCount(pointCount);
        }
    }

    protected Long calcMaxAvailablePoint(CreatingOrder c, PayPriceDTO payPrice) {
        String totalMaxStr = c.getOrderPrice().getExtend(TradeExtendKeyConstants.MAX_AVAILABLE_POINT);
        if (StringUtils.isBlank(totalMaxStr) || "0".equals(totalMaxStr)) {
            return 0L;
        }
        Long totalMaxCount = Long.parseLong(totalMaxStr);
        PointExchange ex = c.getMainOrders().stream()
                .map(main -> main.getOrderAttr().getPointExchange())
                .filter(Objects::nonNull).findFirst().orElse(null);
        if (ex == null) {
            ex = pointExchangeRepository.getExchangeRate();
        }
        if (!ex.isSupportPoint()) {
            return 0L;
        }

        long maxDeductAmt = ex.calcMaxDeductAmt(payPrice.getTotalAmt());
        long maxDeductCount = ex.exAmtToCount(maxDeductAmt);
        return Math.min(maxDeductCount, totalMaxCount);
    }

    protected void add(StepOrderPrice value, PayPriceDTO sumResult) {
        sumResult.setPointAmt(getNullZero(sumResult.getPointAmt()) + getNullZero(value.getPointAmt()));
        sumResult.setPointCount(getNullZero(sumResult.getPointCount()) + getNullZero(value.getPointCount()));
        sumResult.setRealAmt(getNullZero(sumResult.getRealAmt()) + getNullZero(value.getRealAmt()));
    }

    protected void add(OrderPrice value, PayPriceDTO sumResult) {
        sumResult.setPointAmt(getNullZero(sumResult.getPointAmt()) + getNullZero(value.getPointAmt()));
        sumResult.setPointCount(getNullZero(sumResult.getPointCount()) + getNullZero(value.getPointCount()));
        sumResult.setRealAmt(getNullZero(sumResult.getRealAmt()) + getNullZero(value.getOrderRealAmt()));
    }

    protected String getTemplateName(MainOrder mainOrder) {
        if (mainOrder.getStepTemplate() == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
        return mainOrder.getStepTemplate().getTemplateName();
    }

    protected StepOrder buildStepOrder(StepMeta step, StepOrderPrice price, MainOrder mainOrder) {
        StepOrderStatusEnum status = step.getStepNo().intValue() == 1 ?
                StepOrderStatusEnum.STEP_WAIT_PAY : StepOrderStatusEnum.STEP_WAIT_START;
        StepOrder stepOrder = new StepOrder();
        stepOrder.setStepNo(step.getStepNo());
        stepOrder.setStepName(step.getStepName());
        stepOrder.setStatus(status.getCode());
        stepOrder.setPrice(price);
        return stepOrder;
    }

    // 计算每个阶段的价格, 默认实现: 前n阶段由下单入参传入
    protected Map<Integer, StepOrderPrice> getStepPrices(MainOrder mainOrder) {
        StepPriceCalc total = new StepPriceCalc();
        total.realAmt = getNullZero(mainOrder.getOrderPrice().getOrderRealAmt());
        total.pointAmt = getNullZero(mainOrder.getOrderPrice().getPointAmt());
        total.pointCount = getNullZero(mainOrder.getOrderPrice().getPointCount());
        
        List<Long> priceArr = getBaseStepPrices(mainOrder);
        List<StepMeta> steps = mainOrder.getStepTemplate().getStepMetas();
        ParamUtil.expectTrue(priceArr.size() == steps.size() - 1, I18NMessageUtils.getMessage("multi.stage.price.error"));  //# "多阶段价格参数不正确"

        Map<Integer, StepOrderPrice> resultMap = new HashMap<>();
        StepPriceCalc sum = new StepPriceCalc();
        for (int i = 0; i < steps.size(); i++) {
            StepMeta step = steps.get(i);
            // 前面的阶段
            if (i != steps.size() - 1) {
                long price = priceArr.get(i);
                StepPriceCalc stepPrice = new StepPriceCalc();
                if (i == 0) {   // 第1个阶段, 使用积分抵扣
                    if (price < total.pointAmt) {
                        throw new GmallException(OrderErrorCode.ORDER_PRICE_ILLEGAL);
                    }
                    stepPrice.realAmt = price - total.pointAmt;
                    stepPrice.pointAmt = total.pointAmt;
                    stepPrice.pointCount = stepPrice.pointAmt == 0 ? 0L :
                            mainOrder.orderAttr().getPointExchange().exAmtToCount(stepPrice.pointAmt);
                } else {
                    stepPrice.realAmt = price;
                    stepPrice.pointAmt = 0L;
                    stepPrice.pointCount = 0L;
                }
                sum.add(stepPrice);
                resultMap.put(step.getStepNo(), stepPrice.to());
            }
            // 最后一个阶段, 减法
            else {
                total.sub(sum);
                resultMap.put(step.getStepNo(), total.to());
            }
        }
        return resultMap;
    }

    // 除最后一个阶段外, 其余阶段的 基础价格
    protected List<Long> getBaseStepPrices(MainOrder mainOrder) {
        String price = mainOrder.orderAttr().getStepContextProps().get("STEP_PRICE");
        String[] priceArr = StringUtils.split(price, ',');
        return Arrays.stream(priceArr)
                .map(String::trim)
                .filter(StringUtils::isNoneBlank)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    protected static class StepPriceCalc {
        protected long realAmt;
        protected long pointAmt;
        protected long pointCount;

        protected void add(StepPriceCalc other) {
            realAmt += other.realAmt;
            pointAmt += other.pointAmt;
            pointCount += other.pointCount;
        }

        protected void sub(StepPriceCalc other) {
            realAmt -= other.realAmt;
            pointAmt -= other.pointAmt;
            pointCount -= other.pointCount;
        }

        protected StepOrderPrice to() {
            if (realAmt < 0 || pointAmt < 0 || pointCount < 0) {
                throw new GmallException(OrderErrorCode.ORDER_PRICE_ILLEGAL);
            }

            StepOrderPrice p = new StepOrderPrice();
            p.setRealAmt(realAmt);
            p.setPointAmt(pointAmt);
            p.setPointCount(pointCount);
            return p;
        }
    }
}
