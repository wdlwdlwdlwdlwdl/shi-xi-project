package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractPageQueryRestRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author gshine
 * @since 3/8/21 12:04 PM
 */
@Data
@ApiModel("分页查询字典")
public class DictPageQueryRestReq extends AbstractPageQueryRestRequest {

    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("dictKey,精确匹配")
    private String dictKey;

    @ApiModelProperty("类型 1 前端显示用， 2 通用")
    private Integer type;

    @ApiModelProperty("备注,模糊匹配")
    private String remark;

    @Override
    public void checkInput() {
        super.checkInput();
    }
}
