package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author GTS
 * @date 2022/12
 */
@Data
public class BatchApplyCouponRestCommand extends LoginRestCommand {
    /**
     * 券ID领券；必填
     */
    @NotNull(message="coupon.map.required")
    public Map<Long,String> couponMap;

    /**
     * 卖家id;有就传入
     */
    @ApiModelProperty(value = "卖家ID")
    public Long sellerId;

    /**
     * 特定人群分组id
     */
    @ApiModelProperty(value = "特定人群分组id")
    public Long groupId;

    /**
     * 唯一幂等ID;领券幂等；必填
     */
    @NotNull(message="idempotent.id.required")
    public  String  bizId;

    /**
     * 应用来源
     */
    @NotNull(message="app.required")
    private Integer app = 1;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.expectTrue(MapUtils.isNotEmpty(couponMap), I18NMessageUtils.getMessage("coupon")+"Map<Id,Code>"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "券Map<Id,Code>不能为空"
    }
}