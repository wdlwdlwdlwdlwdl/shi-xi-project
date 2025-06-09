package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import com.aliyun.gts.gmall.framework.i18n.LangText;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.Set;

@Data
public class CancelReasonVO {

    private Long id;
    private String cancelReasonCode;
    private String cancelReasonName;
    private Integer deleted;
    private String remark;
    private Date gmtCreate;
    private String createId;
    private Date gmtModified;
    private String updateId;
    private String operator;
    /** i18n */
    private Set<LangText> cancelReasonNameI18n;

    public static CancelReasonVO from(CancelReasonDTO dto) {
        CancelReasonVO vo = new CancelReasonVO();
        BeanUtils.copyProperties(dto, vo);
        return vo;
    }
}
