package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query;

import com.aliyun.gts.gmall.platform.trade.api.dto.common.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("已卖出列表查询")
public class SellerOrderQueryRpcReq extends PageQuery {

    @ApiModelProperty("按购买日期 排正序")
    public static final int SORT_CREATE_TIME_DESC = 1;

    @ApiModelProperty("搜索未评价订单数据")
    public static final int SEARCH_NEVER_VALUATION  = 1;

    @ApiModelProperty("搜索所有未评价订单数据")
    public static final int SEARCH_ALL_NEVER_VALUATION  = 2;

    @ApiModelProperty(value = "卖家ID", required = true)
    @NotNull
    private Long sellerId = -1L;

    @ApiModelProperty(value = "订单编号")
    private String orderId;

    @ApiModelProperty(value = "客户姓氏")
    private String lastName;

    @ApiModelProperty(value = "客户名字")
    private String firstName;

    @ApiModelProperty(value = "订单状态, 逗号分割多值  OrderStatusEnum")
    private String status;

    @ApiModelProperty(value = "客户编号")
    private String custId;

    @ApiModelProperty(value = "订单创建时间")
    //@Size(min = 2 , max = 2)
    private List<Date> orderDate;

    @ApiModelProperty(value = "总价")
    private String totalPrice;

    @ApiModelProperty(value = "折扣")
    private String discountAmount;

    @ApiModelProperty(value = "订单总金额")
    private String orderSubtotal;

    @ApiModelProperty(value = "付款方式")
    private String payChannel;

    @ApiModelProperty(value = "贷款周期")
    private String stagesNumber;

    @ApiModelProperty(value = "贷款状态")
    private String loanStatus;

    @ApiModelProperty(value = "配送方式  LogisticsTypeEnum")
    private String deliveryType;

    @ApiModelProperty(value = "物流费用")
    private String deliveryFee;

    @ApiModelProperty(value = "商家BIN")
    private String sellerBin;

    @ApiModelProperty(value = "商家名称")
    private String sellerName;

    @ApiModelProperty(value = "商品名称关键字")
    private String itemTitle;

    //    @ApiModelProperty(value = "订单状态")
    //    private Integer status;

    @ApiModelProperty(value = "订单创建时间开始")
    private Date startTime;

    @ApiModelProperty(value = "订单创建时间结束")
    private Date endTime;

    @ApiModelProperty(value = "主订单id")
    private Long primaryOrderId;

    @ApiModelProperty(value = "订单类型")
    private Integer orderType;

    @ApiModelProperty(value = "逆向状态")
    @Deprecated // 无效
    private Integer reversalStatus;

    @ApiModelProperty(value = "状态列表", required = false)
    private List<OrderStatusInfo> statusList;

    private List<Long> custIdList;
    private List<Long> primaryOrderIdList;

    /**物流订单列表查询数据**/
    @ApiModelProperty(value = "物流编号")
    private String trackNumber;

    @ApiModelProperty(value = "售后订单ID")
    private String applicationNumber;

    @ApiModelProperty(value = "客户ID")
    private String customerNumber;

    @ApiModelProperty(value = "货到付款手续费")
    private String deliveryCommission;

    @ApiModelProperty("货到付款手续费最小值")
    private Integer deliveryCommissionMin;

    @ApiModelProperty("货到付款手续费最大值")
    private Integer deliveryCommissionMax;

    @ApiModelProperty(value = "顾客收货地址")
    private String customerAddress;

    @ApiModelProperty(value = "商家发货地址")
    private String merchantAddress;

    @ApiModelProperty(value = "订单发货时间")
    //@Size(min = 2 , max = 2)
    private List<Date> departureDate;

    @ApiModelProperty(value = "订单发货时间开始")
    private Date departureStart;

    @ApiModelProperty(value = "订单发货时间结束")
    private Date departureEnd;

    @ApiModelProperty(value = "订单收货时间")
    //@Size(min = 2 , max = 2)
    private List<Date> receivingDate;

    @ApiModelProperty(value = "订单收货时间开始")
    private Date receivingStart;

    @ApiModelProperty(value = "订单收货时间结束")
    private Date receivingEnd;

    @ApiModelProperty(value = "发货城市")
    private String dispatchCity;

    private String dispatchCityCode;

    @ApiModelProperty(value = "收货城市")
    private String destinationCity;

    private String destinationCityCode;

    @ApiModelProperty(value = "物理投递服务名")
    private String deliveryServiceName;

    @ApiModelProperty("排序类别")
    private int sortType = 0;

    @ApiModelProperty("查询类别")
    private int searchType = 0;

    @ApiModelProperty("运费查询最小值")
    private Integer deliveryFeeMin;

    @ApiModelProperty("运费查询最大值")
    private Integer deliveryFeeMax;

    @ApiModelProperty("折扣查询最大值")
    private Integer discountAmountMax;

    @ApiModelProperty("折扣查询最小值")
    private Integer discountAmountMin;

    @ApiModelProperty("订单查询最大值(不含运费)")
    private Integer orderSubtotalMax;

    @ApiModelProperty("订单查询最小值(不含运费)")
    private Integer orderSubtotalMin;

    @ApiModelProperty("订单查询最小值(含运费)")
    private Integer totalPriceMin;

    @ApiModelProperty("订单查询最大值(含运费)")
    private Integer totalPriceMax;

    /**
     * 提供接口参数
     */
    //0 未评价  1 已评价  2 已追评
    @ApiModelProperty(value = "是否评价")
    private Integer evaluate = 0;

}
