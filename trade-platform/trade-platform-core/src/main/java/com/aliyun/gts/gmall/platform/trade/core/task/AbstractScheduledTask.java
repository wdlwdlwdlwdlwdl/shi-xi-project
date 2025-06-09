package com.aliyun.gts.gmall.platform.trade.core.task;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.ability.task.ScheduleTaskAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderConfigService;
import com.aliyun.gts.gmall.platform.trade.core.task.param.TaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractScheduledTask<P extends TaskParam> {

    private static final String KEY_BIZ_CODE = "bizCodes";

    @Autowired
    protected OrderConfigService orderConfigService;

    @Autowired
    private ScheduleTaskAbility scheduleTaskAbility;

    public String getTaskType() {
        return getClass().getSimpleName();
    }

    public ScheduleTask buildTask(P param) {
        List<BizCodeEntity> bizCodes = param.getBizCodes();
        if (CollectionUtils.isEmpty(bizCodes)) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, "task "+ I18NMessageUtils.getMessage("business.identity.empty"));  //# 业务身份不能为空"
        }
        ScheduleTask task = doBuildTask(param);
        task.putParam(KEY_BIZ_CODE, bizCodes);
        return scheduleTaskAbility.beforeCreateTask(task, param.getBizCodes());
    }

    public ExecuteResult execute(ScheduleTask task) throws Exception {
        List<JSON> bizCodesJson = task.getParam(KEY_BIZ_CODE);
        if (bizCodesJson == null) {
            bizCodesJson = new ArrayList<>();
        }
        List<BizCodeEntity> bizCodes = bizCodesJson.stream()
            .map(json -> JSON.toJavaObject(json, BizCodeEntity.class))
            .collect(Collectors.toList());
        ScheduleTask t = scheduleTaskAbility.beforeExecuteTask(task, bizCodes);
        if (t == null) {
            return ExecuteResult.success("skip by scheduleTaskAbility");
        }
        return doExecute(t);
    }

    @AllArgsConstructor
    @Getter
    public static class ExecuteResult {
        private boolean success;    // 如果false将会重试
        private String message;
        private boolean later;      // 是否延后执行，延后执行的不消耗失败重试次数

        public static ExecuteResult success(String message) {
            return new ExecuteResult(true, message, false);
        }

        public static ExecuteResult fail(String message) {
            return new ExecuteResult(false, message, false);
        }

        public static ExecuteResult later(String message) {
            return new ExecuteResult(false, message, true);
        }
    }


    // ======== 子类实现 ========

    protected abstract ScheduleTask doBuildTask(P param);

    protected abstract ExecuteResult doExecute(ScheduleTask task) throws Exception;
}
