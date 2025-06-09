package com.aliyun.gts.gmall.manager.front.customer.facade.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.CustomerAdapter;
import com.aliyun.gts.gmall.manager.front.customer.facade.ImLoginReadFacade;
import com.aliyun.gts.gmall.manager.utils.ResponseUtils;
import com.aliyun.gts.gmall.platform.gim.api.dto.input.OpenLoginRequest;
import com.aliyun.gts.gmall.platform.gim.api.dto.output.UserDTO;
import com.aliyun.gts.gmall.platform.gim.api.dto.response.LoginResp;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImMessageReadFacade;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImOpenLoginFacade;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImUserReadFacade;
import com.aliyun.gts.gmall.platform.gim.common.query.ImUserQuery;
import com.aliyun.gts.gmall.platform.gim.common.query.MessageQuery;
import com.aliyun.gts.gmall.platform.gim.common.type.ImUserOutType;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/8/11 10:54
 */
@Slf4j
@Service
public class ImLoginReadFacadeImpl implements ImLoginReadFacade {
    @Resource
    private ImOpenLoginFacade imOpenLoginFacade;
    @Resource
    private ImMessageReadFacade imMessageReadFacade;

    @Resource
    private CustomerAdapter customerAdapter;
    @Resource
    private ImUserReadFacade imUserReadFacade;

    @Override
    public Map<Long, String> isOnline(List<Long> ids) {
        OpenLoginRequest request = new OpenLoginRequest();
        request.setIds(ids);
        try {
            RpcResponse<Map<Long, String>> response = imOpenLoginFacade.queryLoginServer(request);
            if (response.getData() != null) {
                return response.getData();
            }
            log.info("isOnline is null," + ids.toString());
            return new HashMap<>();
        } catch (Exception e) {
            log.error("isOnline", e);
            return new HashMap<>();
        }
    }

    @Override
    public Long queryCCOImUid(Long sellerId) {
        Long uid = ImUserOutType.seller.generateUid(sellerId);
        List<Long> operatorUids = queryOperatorUid(uid);
        //如果不存在客服;就发给主账号
        if(CollectionUtils.isEmpty(operatorUids)){
            operatorUids = new ArrayList<>();
            operatorUids.add(uid);
        }
        //计算CCO客服ID
        return computeImUser(operatorUids);
    }

    @Override
    public Long getCustImUid(Long custId) {
        return ImUserOutType.customer.generateUid(custId);
    }

    @Override
    public RestResponse<String> queryImChatUrl(Long custId,Long receiveId, Integer type) {
        try {
            OpenLoginRequest request = buildOpenLoginRequest(receiveId, custId, type);
            RpcResponse<LoginResp> response = imOpenLoginFacade.registerAndLogin(request);
            LoginResp resp = response.getData();
            if (resp != null) {
                return RestResponse.okWithoutMsg(resp.getDirectUrl());
            }
            return ResponseUtils.convertVoidResponse(response,false);
        } catch (Exception e) {
            log.error("queryImChatUrl," + receiveId, e);
            return RestResponse.fail("101",I18NMessageUtils.getMessage("system.exception")+e.getMessage());  //# "系统异常"
        }
    }

    @Override
    public Long countUnRead(MessageQuery query) {
        try {
            RpcResponse<Long> resp = imMessageReadFacade.countUnRead(query);
            if (!resp.isSuccess()) {
                log.error("countUnRead fail req: {}  , resp: {}",
                        JSON.toJSONString(query), JSON.toJSONString(resp));
                return 0L;  // 该数据源弱依赖
            }
            return resp.getData();
        } catch (Exception e) {
            log.error("countUnRead error req: {} ",
                    JSON.toJSONString(query), e);
            return 0L;  // 该数据源弱依赖
        }
    }

    private OpenLoginRequest buildOpenLoginRequest(Long receiveId, Long custId, Integer type) {
        OpenLoginRequest loginRequest = new OpenLoginRequest();
        CustomerDTO customer = customerAdapter.queryById(custId);
        ParamUtil.nonNull(customer,I18NMessageUtils.getMessage("user.not.exist")+custId);  //# "用户不存在"
        UserDTO userDTO = new UserDTO();
        userDTO.setOutId(customer.getId());
        userDTO.setOutType(ImUserOutType.customer.getType());
        userDTO.setNickname(customer.getNickname());
        userDTO.setHeadUrl(customer.getHeadUrl());
        userDTO.setUsername(customer.getUsername());
        loginRequest.setUserDTO(userDTO);
        //loginRequest.setIsWap(true);
        loginRequest.setReceiveId(receiveId);
        loginRequest.setTargetType(type);
        return loginRequest;
    }

    private Long computeImUser(List<Long> uids) {
        if (uids.size() == 1) {
            return uids.get(0);
        }
        //在线的客服
        Map<Long, String> onlineMap = isOnline(uids);
        List<Long> online = new ArrayList<>();
        for (Long id : uids) {
            String ip = onlineMap.get(id);
            if (!StringUtils.isEmpty(ip)) {
                online.add(id);
            }
        }
        if (CollectionUtils.isEmpty(online)) {
            online = uids;
        }
        Random r = new Random(1);
        int index = r.nextInt(online.size());
        return online.get(index);
    }

    /**
     *
     * @param mainId
     * @return
     */
    public List<Long> queryOperatorUid(Long mainId){
        if(true){
            return new ArrayList<>();
        }
        ImUserQuery query = new ImUserQuery();
        query.setMainId(mainId);
        RpcResponse<List<UserDTO>> response = imUserReadFacade.list(query);
        if(response.getData() == null){
            return null;
        }
        List<Long> result = new ArrayList<>();
        for(UserDTO userDTO : response.getData()){
            if(!userDTO.getId().equals(mainId)){
                result.add(userDTO.getId());
            }
        }
        return result;
    }
}
