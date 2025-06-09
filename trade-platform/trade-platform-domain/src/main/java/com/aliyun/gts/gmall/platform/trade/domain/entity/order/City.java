package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import lombok.Data;

import java.util.List;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/26 19:25
 */
@Data
public class City extends AbstractPageQueryRpcRequest {

    private String cityCode;
    private String cityName;
    private String cityIndex;
    private Integer active;
    private List<String> codes;
}
