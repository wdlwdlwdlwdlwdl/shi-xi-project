package com.aliyun.gts.gmall.manager.front.customer.facade;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.CustomerCampSubscribeCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.CustomerCampUnSubscribeCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CustomerCampSubscribeQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.CustomerCampSubscribeVO;

public interface CustomerCampSubscribeFacade {
    
    RestResponse<Boolean> campSubscribe(CustomerCampSubscribeCommand campSubscribeCommand);

    RestResponse<Boolean> campUnSubscribe(CustomerCampUnSubscribeCommand campUnSubscribeCommand);


    RestResponse<PageInfo<CustomerCampSubscribeVO>> getSubscribes(CustomerCampSubscribeQuery subscribeQuery);
}
