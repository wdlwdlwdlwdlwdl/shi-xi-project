package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.PointResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: yangl
 * @Date: 20224/8/9 10:37
 * @Desc: 物流地址Resp
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressVO implements Serializable {

    private List<PointResp> points;

}
