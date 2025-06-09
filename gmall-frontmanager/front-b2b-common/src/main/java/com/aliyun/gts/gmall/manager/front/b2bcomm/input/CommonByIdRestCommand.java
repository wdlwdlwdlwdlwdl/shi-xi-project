package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 俊贤
 * @date 2021/02/25
 */
@Data
public class CommonByIdRestCommand extends AbstractQueryRestRequest {
    @NotNull(message = "id不能为空")
    private Long id;
}