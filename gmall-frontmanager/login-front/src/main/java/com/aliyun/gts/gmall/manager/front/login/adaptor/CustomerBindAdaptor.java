package com.aliyun.gts.gmall.manager.front.login.adaptor;

import com.aliyun.gts.gmall.center.user.api.dto.input.CustomerBindThirdAccountReq;
import com.aliyun.gts.gmall.center.user.api.dto.input.CustomerUnbindThirdAccountReq;
import com.aliyun.gts.gmall.center.user.api.dto.input.QueryCustomerLoginAccountReq;
import com.aliyun.gts.gmall.center.user.api.dto.output.CustomerLoginAccountInfoDTO;
import com.aliyun.gts.gmall.center.user.api.enums.ThirdCustomerAccountTypeEnum;
import com.aliyun.gts.gmall.center.user.api.facade.CustomerBindThirdAccountFacade;
import com.aliyun.gts.gmall.framework.api.consts.ChannelEnum;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.login.facade.LoginFacade;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerAccountTypeEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月22日 15:44:00
 */
@Slf4j
@Service
public class CustomerBindAdaptor {

    @Resource
    private CustomerBindThirdAccountFacade customerBindThirdAccountFacade;

    @Resource
    private LoginFacade loginFacade;

    @Autowired
    private UserAdapter userAdapter;

    public CustomerDTO bindCustomerRelation(CustomerBindThirdAccountReq req) {
        RpcResponse<CustomerDTO> response = customerBindThirdAccountFacade.customerBindThirdAccount(req);
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }

    public RestResponse<Boolean> unbindCustomerRelation(String phone, String openId, String type, Long custId, String channel) {
        log.info("【解绑】unbindCustomerRelation custId: {} ,type: {}, channel: {}", custId, type, channel);
        checkUser(phone, custId);
        CustomerUnbindThirdAccountReq req = new CustomerUnbindThirdAccountReq();
        req.setPhone(phone);
        req.setType(type);
        req.setOpenId(openId);
        RpcResponse<Boolean> response = customerBindThirdAccountFacade.customerUnbindThirdAccount(req);
        if (!response.isSuccess() && Objects.nonNull(response.getFail())) {
            FailInfo fail = response.getFail();
            return RestResponse.fail(fail.getCode(), fail.getMessage());
        }
        loginFacade.refreshToken(custId, getChannelEnumByTypeAndChannel(type, channel), null);
        return RestResponse.okWithoutMsg(Boolean.TRUE);
    }

    private ChannelEnum getChannelEnumByTypeAndChannel(String type, String channel) {
        // 根据 type 来矫正 channel 的值
        if (CustomerAccountTypeEnum.WECHAT.getCode().equals(type)) {
            return ChannelEnum.WX_MINI;
        } else if (ThirdCustomerAccountTypeEnum.TMALL_GENIE.getCode().equals(type)) {
            return ChannelEnum.H5;
        } else {
            return ChannelEnum.get(channel);
        }
    }

    private void checkUser(String phone, Long custId) {
        if (StringUtils.isNotBlank(phone)) {
            CustomerDTO customerDTO = userAdapter.queryById(custId);
            if (Objects.nonNull(customerDTO)) {
                if (!phone.equals(customerDTO.getPhone())) {
                    throw new FrontManagerException(LoginFrontResponseCode.WECHAT_UNBIND_FAIL);
                }
            }
        }
    }

    public List<CustomerLoginAccountInfoDTO> queryCustomerLoginAccount(Long custId, String type) {
        QueryCustomerLoginAccountReq req = new QueryCustomerLoginAccountReq();
        req.setCustId(custId);
        req.setType(type);
        RpcResponse<List<CustomerLoginAccountInfoDTO>> response = customerBindThirdAccountFacade.queryCustomerLoginAccountById(req);
        if (response.isSuccess()) {
            return response.getData();
        }
        return Lists.newArrayList();
    }

}
