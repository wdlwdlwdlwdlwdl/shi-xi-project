package com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/25 10:55
 */
@Data
public class CityRpcReq extends AbstractCommandRpcRequest {
    private static final long serialVersionUID=1L;
    private Long id;
    private String cityCode;
    private String cityName;
    private String cityIndex;
    private Integer active;
    private Integer deleted;
    private String polygon;
    private String latitude;
    private String longitude;
    private String zoom;
    private String color;
    private Date gmtCreate;
    private String createId;
    private Date gmtModified;
    private String updateId;
    private String operator;
    private List<String> codes;
}
