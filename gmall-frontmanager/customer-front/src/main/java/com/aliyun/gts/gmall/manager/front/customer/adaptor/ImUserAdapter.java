package com.aliyun.gts.gmall.manager.front.customer.adaptor;

import com.aliyun.gts.gmall.platform.gim.api.client.ImUserServiceClient;
import com.aliyun.gts.gmall.platform.gim.api.dto.output.UserDTO;
import com.aliyun.gts.gmall.platform.gim.common.type.ImUserOutType;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Slf4j
@ApiOperation(value = "用户消息接口")
@Component
public class ImUserAdapter extends ImUserServiceClient {

    public Long getCustomerUid(Long sellerId) {
        return ImUserOutType.customer.generateUid(sellerId);
    }

    public Map<Long, UserDTO> queryUserByIds(Set<Long> ids) {
        return super.queryUserByIds(ids);
    }

}
