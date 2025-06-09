package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingApplyDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.Contact;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.PurchaseRequirement;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.QualifyRequirement;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.SourcingFeature;
import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingStatus;
import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingType;
import com.aliyun.gts.gmall.manager.front.b2bcomm.constants.AccountTypeEnum;
import com.aliyun.gts.gmall.manager.front.b2bcomm.constants.ButtonNames;
import com.aliyun.gts.gmall.manager.front.b2bcomm.model.OperatorDO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/14 12:52
 */
@Data
public class SourcingVo extends FulfillRequireVO {

    /**
     *
     */
    private String title;

    /**
     * 寻源类型1询价，2竞价 3 招投标
     */
    private Integer sourcingType;
    /**
     * 采购商id
     */
    private Long purchaserId;

    private String purchaserName;

    /**
     * 报价开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 报价结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 报名时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyStartTime;
    /**
     * 报名截至时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyEndTime;

    /**
     *1公开，2邀约
     */
    private Integer applyType;

    /**
     *1临时，2长期采购
     */
    private Integer purchaseType;

    private Contact contact;

    private PurchaseRequirementVO purchaseRequire;

    private QualifyRequirement qualifyRequire;
    /**
     *
     */
    private SourcingFeature feature;
    /**
     *操作者
     */
    private Long operatorId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 审核状态
     */
    private Integer approveStatus;

    /**
     * 关联的请购单
     */
    private Long relateId;
    /**
     * 材料
     */
    private List<SourcingMaterialVo> materials;
    /**
     * 展示状态
     */
    private Integer viewStatus;
    /**
     * 邀约的供应商列表
     */
    private List<SourcingApplyDTO> applySupplier;
    /**
     *创建者ID
     */
    private String createId;
    /**
     * 创建者名称
     */
    private String createName;
    /**
     * 审核跳转地址
     */
    private String auditUrl;

    /**
     * 已报价数量
     */
    private Integer quoteCount;

    public void button(OperatorDO operatorDO){
        if(operatorDO == null){
            return;
        }
        if(sourcingType.equals(SourcingType.Zhao.getValue())){
            addButton(ButtonNames.tenderingManage);
            addButton(ButtonNames.tenderingDetail);
            if(!operatorDO.getType().equals(AccountTypeEnum.PURCHASER_ACCOUNT.getCode())){
                buttons.put(ButtonNames.tenderingDetail, false);
                buttons.put(ButtonNames.tenderingManage,false);
            }
            //评标配置
            if(status.equals(SourcingStatus.bid_opening.getValue())){
                if(operatorDO.getType().equals(AccountTypeEnum.PURCHASER_ACCOUNT.getCode())){
                    addButton(ButtonNames.tenderingConfig);
                    addButton(ButtonNames.tenderingAbortion);
                    addButton(ButtonNames.tenderingClose);
                }
            }else if(status.equals(SourcingStatus.bid_chosing.getValue())){
                if(operatorDO.getType().equals(AccountTypeEnum.PURCHASER_ACCOUNT.getCode())){
                    addButton(ButtonNames.tenderingConfig);
                    addButton(ButtonNames.tenderingAbortion);
                    addButton(ButtonNames.tenderingClose);
                }else if(operatorDO.getType().equals(AccountTypeEnum.EXPERT_ACCOUNT.getCode())){
                    addButton(ButtonNames.tenderingChosing);
                }
            }else if(status.equals(SourcingStatus.submit_approve.getValue())){
                if(operatorDO.getType().equals(AccountTypeEnum.PURCHASER_ACCOUNT.getCode())){
                    addButton(ButtonNames.cancel);
                }
            }else if(status.equals(SourcingStatus.draft.getValue())){
                if(operatorDO.getType().equals(AccountTypeEnum.PURCHASER_ACCOUNT.getCode())){
                    addButton(ButtonNames.tenderingEdit);
                }
            }else if(status.equals(SourcingStatus.bid_chosen.getValue())){
                if(!operatorDO.getType().equals(AccountTypeEnum.EXPERT_ACCOUNT.getCode())){
                    addButton(ButtonNames.tenderingSummary);
                }
            }else if(status.equals(SourcingStatus.award.getValue())){
                addButton(ButtonNames.tenderingDeclare);
            }
        }
    }

}
