package com.aliyun.gts.gmall.center.trade.server.xxljob;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.trade.server.xxljob.component.OrderCheckComponent;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.TcConfirmPayRecordDTO;
import com.aliyun.gts.gmall.platform.pay.api.facade.TcConfirmPayFacade;
import com.aliyun.gts.gmall.platform.pay.common.query.TcConfirmQuery;
import com.aliyun.gts.gmall.platform.trade.core.config.WorkflowProperties;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 执行异步任务
 */
@Slf4j
@Component
public class OrderCheckXxlJob {

    // 确认支付时间
    private final Integer confirmPayTime = -15;

    private final Integer confirmStatus = 1;
    @Autowired
    private OrderCheckComponent orderCheckComponent;

    @Autowired
    private TcConfirmPayFacade tcConfirmPayFacade;

    @Autowired
    protected WorkflowProperties workflowProperties;

    private ExecutorService threadPool = Executors.newFixedThreadPool(2);

    /**
     * 订单支付检查
     * 每 10 秒执行一次 每次抓取 5条
     * @param param
     * @return
     */
    @XxlJob(value = "shardingOrderCheckXxlJob")
    public ReturnT<String> shardingOrderCheckXxlJob (String param) {
        // 获取分片参数：分片总数和分片序列号
        ParamUtils.Params params = ParamUtils.parse(XxlJobHelper.getJobParam());
        Integer batchSize = params.getInt("batchSize", 10);
        //int shardIndex = 0;
        //int shardTotal = 1;
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.info(" current index index={}, shardTotal={}, param={}", shardIndex, shardTotal, param);

        Date currentDate = new Date();
        // 使用Calendar对象增加15分钟
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Aqtobe"));
        calendar.setTime(currentDate);
        calendar.add(Calendar.MINUTE, confirmPayTime);
        // 获取修改后的时间
        Date minutes15Date = calendar.getTime();

        // 处理 15分钟之内的支付单
        List<TcConfirmPayRecordDTO> in15MindataList = getIn15MinDataList(batchSize, minutes15Date);
        // 处理 15分钟之外的支付单
        List<TcConfirmPayRecordDTO> out15MindataList = getOut15MinDataList(batchSize, minutes15Date);

        if (CollectionUtils.isNotEmpty(in15MindataList)) {
            ConfirmOrderInfo confirmOrderInfo = new ConfirmOrderInfo(in15MindataList, shardIndex, shardTotal, true);
            threadPool.execute(confirmOrderInfo);
        }
        if (CollectionUtils.isNotEmpty(out15MindataList)) {
            ConfirmOrderInfo confirmOrderInfo = new ConfirmOrderInfo(out15MindataList, shardIndex, shardTotal, false);
            threadPool.execute(confirmOrderInfo);
        }

        return ReturnT.SUCCESS;
    }

    private List<TcConfirmPayRecordDTO> getIn15MinDataList(Integer batchSize, Date minutes15Date) {
        TcConfirmQuery tcConfirmQuery = new TcConfirmQuery();
        tcConfirmQuery.setBatchSize(batchSize);
        tcConfirmQuery.setGmtCreateLe(minutes15Date);
        tcConfirmQuery.setStatus(confirmStatus);
        RpcResponse<List<TcConfirmPayRecordDTO>> response = tcConfirmPayFacade.query(tcConfirmQuery);
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }

    private List<TcConfirmPayRecordDTO> getOut15MinDataList(Integer batchSize, Date minutes15Date) {
        TcConfirmQuery tcConfirmQuery = new TcConfirmQuery();
        tcConfirmQuery.setBatchSize(batchSize);
        tcConfirmQuery.setGmtCreateGt(minutes15Date);
        RpcResponse<List<TcConfirmPayRecordDTO>> response = tcConfirmPayFacade.query(tcConfirmQuery);
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }


    @Data
    public class ConfirmOrderInfo implements Runnable {

        private List<TcConfirmPayRecordDTO> dataList;
        private Integer shardIndex;
        private Integer shardTotal;
        private boolean isIn15Min;

        public ConfirmOrderInfo(List<TcConfirmPayRecordDTO> dataList,
                                Integer shardIndex, Integer shardTotal, boolean isIn15Min) {
            this.dataList = dataList;
            this.shardIndex = shardIndex;
            this.shardTotal = shardTotal;
            this.isIn15Min = isIn15Min;
        }

        @Override
        public void run() {
            try {
                orderCheckComponent.shardingExecute(dataList, shardIndex, shardTotal, isIn15Min);
            }
            catch (Exception e) {
                log.error("Partition task execution exception: {}", JSONObject.toJSONString(dataList), e);
            }
        }

    }











}
