package com.aliyun.gts.gmall.manager.front.login.dto.input.query;


import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractCommandRestRequest;
import lombok.Data;

@Data
public class GrantAssetsMsg  extends AbstractCommandRestRequest {

    private String phone;

    /**
     * halyk唯一标识
     */
    private String custPrimary;

    private Integer checkReceivePointType;


}
