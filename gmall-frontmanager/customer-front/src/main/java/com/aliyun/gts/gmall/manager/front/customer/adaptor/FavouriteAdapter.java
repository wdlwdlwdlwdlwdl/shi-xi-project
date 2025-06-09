package com.aliyun.gts.gmall.manager.front.customer.adaptor;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.MessageSendManager;
import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.customer.converter.CustomerFavouriteConverter;
import com.aliyun.gts.gmall.manager.front.customer.converter.TrackingLogConverter;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddFavouriteRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.DeleteFavouriteRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.TrackingLogCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CustomerFavouriteQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.CustomerFavouriteVO;
import com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.customer.dto.utils.ResponseUtils;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerByIdQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.message.CustFavouriteMessageDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerFavouriteDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerFavouriteReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerFavouriteWriteFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerItemVisitHisReadFacade;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;

/**
 * @Description
 * @Author FaberWong
 * @Date 2024/8/29 13:59
 */
@Slf4j
@Component
@ApiOperation(value = "收藏夹")
public class FavouriteAdapter {

    @Value("${front-manager.message.user-favourite.topic:}")
    private String userFavouriteTopic;

    @Autowired
    private MessageSendManager messageSendManager;

    @Autowired
    private CustomerFavouriteConverter converter;

    @Autowired
    private CustomerFavouriteReadFacade customerFavouriteReadFacade;

    @Autowired
    private CustomerFavouriteWriteFacade customerFavouriteWriteFacade;

    /**
     * 加入收藏
     * @param command
     * @return
     */
    public Boolean addFavourite(AddFavouriteRestCommand command) {
        CustFavouriteMessageDTO custFavouriteMessageDTO = converter.command2Message(command);
        //当前登录客户
        CustDTO user = UserHolder.getUser();
        if (user != null) {
            custFavouriteMessageDTO.setCustId(user.getCustId());
        }
        CustomerByIdQuery query = new CustomerByIdQuery();
        query.setId(user.getCustId());
        RpcResponse<Boolean> rpcResponse = customerFavouriteReadFacade.exceedLimit(query);
        if (rpcResponse != null && rpcResponse.isSuccess() && Boolean.TRUE.equals(rpcResponse.getData())) {
            //收藏因为前端特殊处理，所以后端不抛异常，按照成功走，不能返回msg，data返回false就可以
            throw new GmallException(CustomerFrontResponseCode.FAVOURITE_EXCEED_LIMIT);
        }
        custFavouriteMessageDTO.setGmtCreate(new Date());
        return messageSendManager.sendMessage(custFavouriteMessageDTO, userFavouriteTopic, "ADD");
    }

    /**
     * 收藏列表
     * @param query
     * @return
     */
    public RestResponse<PageInfo<CustomerFavouriteVO>> listFavourite(CustomerFavouriteQuery query) {
        RpcResponse<PageInfo<CustomerFavouriteDTO>> rpcResponse =  customerFavouriteReadFacade.pageQuery(converter.to(query));
        return ResponseUtils.convertVOPageResponse(rpcResponse, converter::toVO, false);
    }

    /**
     *  移除收藏
     * @param command
     * @return
     */
    public Boolean deleteFavourite(DeleteFavouriteRestCommand command) {
        CustFavouriteMessageDTO custFavouriteMessageDTO = converter.command2Message(command);
        //当前登录客户
        CustDTO user = UserHolder.getUser();
        if (user != null) {
            custFavouriteMessageDTO.setCustId(user.getCustId());
        }
        custFavouriteMessageDTO.setGmtCreate(new Date());
        return messageSendManager.sendMessage(custFavouriteMessageDTO, userFavouriteTopic, "DELETE");
    }
}
