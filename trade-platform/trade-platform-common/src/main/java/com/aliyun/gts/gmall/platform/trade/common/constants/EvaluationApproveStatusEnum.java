package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum EvaluationApproveStatusEnum  implements I18NEnum {

//    NEED_APPROVE(0, "|pending.approval|"),   //# "待审批"
    NEED_APPROVE(0, I18NMessageUtils.getMessage("pending.approval")),   //# "待审批"

//    APPROVING(1, "|in.approval|"),   //# "审批中"
    APPROVING(1, I18NMessageUtils.getMessage("in.approval")),   //# "审批中"

//    PASSED(2, "|approved|"),   //# "已通过"
    PASSED(2, I18NMessageUtils.getMessage("acceptance")),   //# "已通过"

//    REFUSED(3,"|rejected|");  //# "已拒绝"
    REFUSED(3,I18NMessageUtils.getMessage("rejection"));  //# "已拒绝"

    private Integer code;
    
    private String script;


    public static EvaluationApproveStatusEnum codeOf(Integer code) {
        return Arrays.stream(EvaluationApproveStatusEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

}
