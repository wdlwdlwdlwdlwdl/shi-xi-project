package com.aliyun.gts.gmall.platform.trade.domain.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分库分表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DbTable {

    private String dbName;
    private String tableName;
}
