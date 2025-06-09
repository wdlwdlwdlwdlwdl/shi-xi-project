package com.aliyun.gts.gmall.manager.front.customer.adaptor;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.MessageSendManager;
import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.customer.converter.ItemVisitHisConverter;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddVisitHisRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.ItemVisitHisVO;
import com.aliyun.gts.gmall.manager.utils.ResponseUtils;
import com.aliyun.gts.gmall.platform.user.api.dto.message.ItemVisitHisMessageDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerItemVisitHisDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerItemVisitHisReadFacade;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description
 * @Author FaberWong
 * @Date 2024/8/29 13:59
 */
@Slf4j
@ApiOperation(value = "商品浏览历史")
@Component
public class CustomerItemVisitHisAdapter {


    @NacosValue(value = "${front-manager.message.item-visit.topic}", autoRefreshed = true)
    @Value("${front-manager.message.item-visit.topic:}")
    private String itemVisitHisTopic;


    @Autowired
    private MessageSendManager messageSendManager;

    @Autowired
    private ItemVisitHisConverter converter;

    @Autowired
    private CustomerItemVisitHisReadFacade customerItemVisitHisReadFacade;


    public Boolean addVisitHis(AddVisitHisRestCommand command) {
        ItemVisitHisMessageDTO itemVisitHisMessageDTO = converter.command2Message(command);
        //当前登录客户
        CustDTO user = UserHolder.getUser();
        if (user != null) {
            itemVisitHisMessageDTO.setCustId(user.getCustId());
        }
        itemVisitHisMessageDTO.setGmtCreate(new Date());
        return messageSendManager.sendMessage(itemVisitHisMessageDTO, itemVisitHisTopic, "ADD");
    }

    public RestResponse<PageInfo<ItemVisitHisVO>> listVisitHis(PageLoginRestQuery query) {
        RpcResponse<PageInfo<CustomerItemVisitHisDTO>> rpcResponse =  customerItemVisitHisReadFacade.pageQuery(converter.toCustomerByCustIdQuery(query));
        return ResponseUtils.convertVOPageResponse(rpcResponse, converter::toVO, false);
    }


}
