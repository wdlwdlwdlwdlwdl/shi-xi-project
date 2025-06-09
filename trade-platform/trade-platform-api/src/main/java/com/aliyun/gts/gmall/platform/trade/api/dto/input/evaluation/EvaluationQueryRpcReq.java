package com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class EvaluationQueryRpcReq extends AbstractPageQueryRpcRequest {

    @ApiModelProperty("按时间倒序")
    public static final int SORT_TYPE_TIME_DSC = 1;
    @ApiModelProperty("按评分倒序, 次按时间倒序")
    public static final int SORT_TYPE_SCORE_TIME_DSC = 2;
    @ApiModelProperty("按购买日期 排正序")
    public static final int SORT_TYPE_TIME_ASC = 3;

    @ApiModelProperty("查询所有")
    public static final int GRADE_CLASSIFY_ALL = 1;
    @ApiModelProperty("查询好评")
    public static final int GRADE_CLASSIFY_GOOD = 2;
    @ApiModelProperty("查询差评")
    public static final int GRADE_CLASSIFY_BAD = 3;

    @ApiModelProperty("商品ID")
    private Long itemId;

    @ApiModelProperty("skuID")
    private Long skuId;

    //商品名称  长度小于32
    @ApiModelProperty("商品标题")
    private String itemTitle;

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("卖家名称")
    private String sellerName;

    @ApiModelProperty("卖家的IIN/BIN")
    private Long sellerBIN;

    @ApiModelProperty("有图/视频")
    private Boolean hasMedia;

    @ApiModelProperty("有追评")
    private Boolean hasAddition;

    @ApiModelProperty("评分等级")
    private String[] productRatingArrays;

    @ApiModelProperty("评分范围 -- 最小值, >=")
    private Integer rateScoreMin;

    @ApiModelProperty("评分范围 -- 最大值, <=")
    private Integer rateScoreMax;

    @ApiModelProperty("是否系统自动评价")
    private Boolean systemEvaluation;

    @ApiModelProperty("主订单号")
    private Long primaryOrderId;

    //订单ID  长度小于32
    @ApiModelProperty("子订单号")
    private Long subOrderId;

    @ApiModelProperty("评价时间范围 -- 开始, >=")
    private Date evaluateTimeStart;

    @ApiModelProperty("评价时间范围 -- 结束, <=")
    private Date evaluateTimeEnd;

    @ApiModelProperty("用户ID")
    private Long custId;

    @ApiModelProperty("用户名称")
    private String custName;

    //评论状态  下拉多选
    @ApiModelProperty("评论状态")
    private String[] approvalStatus;

    @ApiModelProperty("扩展字段")
    private Map<String, List<Object>> extraFilters;

    @ApiModelProperty("排序类别")
    private int sortType = SORT_TYPE_TIME_DSC;

    @ApiModelProperty("等级分类")
    private int gradeClassify = 0;

    private boolean hasItemId;

    private boolean hasItemIdSearch;

    private List<Long> primaryOrderList;

    private boolean orderSearch = false;
}
