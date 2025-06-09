package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tc_reversal_flow")
public class TcReversalFlowDO implements Serializable {

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 售后单ID
     */
    private Long primaryReversalId;

    /**
     * @see ReversalStatusEnum
     */
    private Integer fromReversalStatus;

    /**
     * @see ReversalStatusEnum
     */
    private Integer toReversalStatus;

    /**
     * 买家OR卖家操作：1-买家, 0-卖家
     */
    private Integer custOrSeller;

//    public void setOpName(String opName) {
//        ReversalStatusEnum reversalStatusEnum = ReversalStatusEnum.valueOf(opName);
////        System.out.println("===="+LocaleContextHolder.getLocale().getLanguage());
////        System.out.println("===="+reversalStatusEnum.getName());
////        this.opName = opName;
//    }


    /**
     * 操作名称
     */
    private String opName;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /**
     * 扩展字段
     */
    private String features;

}
