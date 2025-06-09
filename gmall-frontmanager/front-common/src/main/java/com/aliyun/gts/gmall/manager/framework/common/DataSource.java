package com.aliyun.gts.gmall.manager.framework.common;

import java.util.List;
import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;

/**
 * 
 * @Title: DataSource.java
 * @Description: 数据源接口定义
 * @author zhao.qi
 * @date 2024年11月14日 10:39:34
 * @version V1.0
 */
public interface DataSource {
    <T> T query(AbstractRequest request);

    <T> T query(AbstractRequest request, T defaultValue);

    <T> List<T> queryList(AbstractRequest request);

    <T> List<T> queryList(AbstractRequest request, List<T> defaultValue);
}
