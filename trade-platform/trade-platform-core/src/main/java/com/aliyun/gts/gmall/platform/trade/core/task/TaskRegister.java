package com.aliyun.gts.gmall.platform.trade.core.task;

import com.aliyun.gts.gmall.platform.trade.core.task.biz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TaskRegister {

    // 任务注册
    private static final Set<Class<? extends AbstractScheduledTask>> TASK_CLASSES = new HashSet<>();

    // map
    private static Map<String, AbstractScheduledTask> cachedMap;

    static {
        addTaskClass(AutoEvaluateOrderTask.class);
        addTaskClass(CloseUnpaidOrderTask.class);
        addTaskClass(ReversalAgreeTask.class);
        addTaskClass(ReversalReceiveTask.class);
        addTaskClass(SystemConfirmOrderTask.class);
        addTaskClass(UnlockInventoryTask.class);
        addTaskClass(AutoCancelOrderTask.class);
        addTaskClass(ReversalCancelTask.class);
    }

    public static void addTaskClass(Class<? extends AbstractScheduledTask> clazz) {
        synchronized (TASK_CLASSES) {
            TASK_CLASSES.add(clazz);
            cachedMap = null;
        }
    }

    public static void removeTaskClass(Class<? extends AbstractScheduledTask> clazz) {
        synchronized (TASK_CLASSES) {
            TASK_CLASSES.remove(clazz);
            cachedMap = null;
        }
    }


    // ====================================================================

    @Autowired
    private ApplicationContext applicationContext;

    public Map<String, AbstractScheduledTask> getTaskMap() {
        Map<String, AbstractScheduledTask> temp = cachedMap;
        if (temp != null) {
            return temp;
        }
        synchronized (TASK_CLASSES) {
            Map<String, AbstractScheduledTask> map = new HashMap<>();
            for (Class<? extends AbstractScheduledTask> clazz : TASK_CLASSES) {
                AbstractScheduledTask task = applicationContext.getBean(clazz);
                map.put(task.getTaskType(), task);
            }
            cachedMap = Collections.unmodifiableMap(map);
            return cachedMap;
        }
    }
}
