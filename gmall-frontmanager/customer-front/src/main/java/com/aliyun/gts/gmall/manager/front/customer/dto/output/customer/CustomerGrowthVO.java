package com.aliyun.gts.gmall.manager.front.customer.dto.output.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerGrowthVO {


    /** 买家ID */
    private Long custId;

    /** 成长值总和 */
    private Integer growthSum;

//    /** 统计的开始时间 */
//    private Date startDate;
//
//    /** 统计的结束时间 */
//    private Date endDate;

    /**
     * 类型
     */
    private String type;

}
