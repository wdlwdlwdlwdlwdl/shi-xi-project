package com.aliyun.gts.gmall.manager.front.customer.dto.input.query;

import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustomerGrowthQuery extends PageLoginRestQuery {

      @ApiModelProperty("成长值类型 CustomerGrowthTypeEnum 3:当日首次登录,2:订单次数,1:订单金额")
      private String type;


}
