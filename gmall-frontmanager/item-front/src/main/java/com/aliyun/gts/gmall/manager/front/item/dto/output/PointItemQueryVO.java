package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.center.item.api.dto.output.PointItemQueryDTO;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.PromDetailVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("积分商品和优惠券对象")
public class PointItemQueryVO extends PointItemQueryDTO {

    private Long realPrice;

    private String realPriceStr;

    private Long pointCount;

    private String pointCountStr;

    private String startDate;

    private String endDate;

    private String originalPrice;

    private Long itemQuantity;

    private String manYuan;

    private String jianYuan;

    private String limit;

    private List<String> couPonPictureList;

    private List<PointCouponItemVO> pointCouponItemVOList;

    private List<PromDetailVO> itemDetails;
}