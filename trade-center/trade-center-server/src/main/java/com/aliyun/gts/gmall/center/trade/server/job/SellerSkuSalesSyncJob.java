package com.aliyun.gts.gmall.center.trade.server.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.schedulerx.worker.domain.JobContext;
import com.alibaba.schedulerx.worker.processor.JavaProcessor;
import com.alibaba.schedulerx.worker.processor.ProcessResult;
import com.aliyun.gts.gmall.center.trade.core.domainservice.SellerSkuService;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
@Slf4j
public class SellerSkuSalesSyncJob {

    @Autowired
    private SellerSkuService sellerSkuService;

    @XxlJob(value = "sellerSkuSalesSyncJob")
    public ProcessResult process(JobContext context) throws Exception {
        Date startDate = null;
        Date endDate = new Date();
        ParamUtils.Params params = ParamUtils.parse(XxlJobHelper.getJobParam());
        String startTimeStr = params.getString("startTimeStr", "");
        if (StringUtils.isNotBlank(startTimeStr)) {
            DateTime parse = DateUtil.parse(startTimeStr, "yyyy-MM-dd");
            startDate = parse.toJdkDate();
        }

        long sellerId = params.getLong("sellerId", 0l);
        sellerSkuService.autoSync(startDate, endDate, sellerId);
        return new ProcessResult(true);
    }
}
