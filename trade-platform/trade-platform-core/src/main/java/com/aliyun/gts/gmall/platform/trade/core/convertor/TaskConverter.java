package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface TaskConverter {

    @Mappings({
            @Mapping(target = "type", source = "taskType"),
            @Mapping(target = "params", source = "taskParams", qualifiedByName = "toMap"),
    })
    ScheduleTask toScheduleTask(TcAsyncTaskDO task);

    @Mappings({
            @Mapping(target = "taskType", source = "type"),
            @Mapping(target = "taskParams", source = "params", qualifiedByName = "toString"),
            @Mapping(target = "executeCount", constant = "0"),
            @Mapping(target = "taskStatus", constant = "1"),
    })
    TcAsyncTaskDO toTcAsyncTaskDO(ScheduleTask task);

    @Named("toMap")
    default Map<String, Object> toMap(String str) {
        return JSON.parseObject(str, Map.class);
    }

    @Named("toString")
    default String toString(Map<String, Object> map) {
        return JSON.toJSONString(map);
    }
}
