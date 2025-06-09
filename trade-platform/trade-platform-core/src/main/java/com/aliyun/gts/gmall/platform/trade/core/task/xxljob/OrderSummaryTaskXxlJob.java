package com.aliyun.gts.gmall.platform.trade.core.task.xxljob;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.config.I18NConfig;
import com.aliyun.gts.gmall.platform.gim.api.dto.input.ImCommonMessageRequest;
import com.aliyun.gts.gmall.platform.gim.api.enums.ImTemplateTypeEnum;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderStatisticsQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.OrderStatisticsDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.convertor.TaskConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.ImSendManager;
import com.aliyun.gts.gmall.platform.trade.core.task.TaskRegister;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils.Params;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderSummaryDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TcSumOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcAsyncTaskRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcSumOrderRepository;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单统计任务
 */
@Component
public class OrderSummaryTaskXxlJob extends BaseMapTwiceXxlJob {

    @Autowired
    private TcAsyncTaskRepository tcAsyncTaskRepository;

    @Autowired
    private TaskConverter taskConverter;

    @Autowired
    private TaskRegister taskRegister;

    @Autowired
    private OrderTaskService orderTaskService;

    @Autowired
    private I18NConfig i18NConfig;

    @Autowired
    private OrderReadFacade orderReadFacade;

    @Autowired
    private OrderWriteFacade orderWriteFacade;

    @Autowired
    private TcSumOrderRepository tcSumOrderRepository;

    @Autowired
    private ImSendManager imSendManager;

    private final static int IS_SEND_0 = 0;//未发送
    private final static int IS_SEND_1 = 1;//发送成功
    private final static int IS_SEND_2 = 2;//不需要发送


    @XxlJob(value = "orderSummaryTaskXxlJob")
    public ReturnT<String> orderSummaryTaskXxlJob(String param) {
        LocaleContextHolder.setLocale(new Locale(i18NConfig.getDefaultLang()));
        Params params = ParamUtils.parse(XxlJobHelper.getJobParam());
        String sellerIds = params.getString("sellerIds",null);
        OrderStatisticsQueryRpcReq req = new OrderStatisticsQueryRpcReq();
        if(StringUtils.isNotBlank(sellerIds)){
            String[] sellerIdArr = sellerIds.split(",");
            List<Long> sellerIdList = new ArrayList<>();
            for(String sellerId:sellerIdArr){
                sellerIdList.add(Long.valueOf(sellerId));
            }
            req.setSellerIds(sellerIdList);
        }
        req.setOrderStatus(OrderStatusEnum.CANCELLED.getCode());
        RpcResponse<List<OrderStatisticsDTO>> response=  orderReadFacade.statisticsBySellerIds(req);
        if(response.isSuccess()){
            //添加是否重复生成逻辑
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            TcSumOrder sumOrder = new TcSumOrder();
            sumOrder.setStatisticDate(format.format(new Date()));
            List<TcOrderSummaryDO> validatelist = tcSumOrderRepository.querySummaryList(sumOrder);
            if(CollectionUtils.isEmpty(validatelist)) {
                orderWriteFacade.summaryOrder(response.getData());
            }
            //批量查询统计
            TcSumOrder newSumOrder = new TcSumOrder();
            newSumOrder.setIsSend(IS_SEND_0);
            newSumOrder.setStatisticDate(format.format(new Date()));
            List<TcOrderSummaryDO> list = tcSumOrderRepository.querySummaryList(newSumOrder);
            if(!CollectionUtils.isEmpty(list)) {
                for (TcOrderSummaryDO tcOrderSummaryDO : list) {
                    if (Float.parseFloat(tcOrderSummaryDO.getCancelRate()) >= 80) {
                        if (sendSellerEmail(tcOrderSummaryDO)) {
                            tcOrderSummaryDO.setIsSend(IS_SEND_1);
                        }
                    } else {
                        tcOrderSummaryDO.setIsSend(IS_SEND_2);
                    }
                    tcSumOrderRepository.saveSummary(tcOrderSummaryDO);
                }
            }
        }
        return ReturnT.SUCCESS;
    }


    /**
     * 发送邮件
     * @param tcOrderSummaryDO
     * @return
     */
    protected boolean sendSellerEmail(TcOrderSummaryDO tcOrderSummaryDO){
        Map<String, String> replacements = new HashMap<>();
        ImCommonMessageRequest msg = new ImCommonMessageRequest();
        msg.setCode("switch_inventory_reminder_seller");
        imSendManager.initParam(tcOrderSummaryDO.getSellerId(),msg,false);
        msg.setTemplateType(ImTemplateTypeEnum.EMAIL.getCode());
        msg.setReplacements(replacements);
        msg.setHasSyncNotice(true);
        RpcResponse<Boolean>  result = imSendManager.sendMessage(msg);
        return result.isSuccess();
    }
}
