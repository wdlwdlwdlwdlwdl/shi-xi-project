package com.aliyun.gts.gmall.manager.front.b2bcomm.controller;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.constants.AccountTypeEnum;
import com.aliyun.gts.gmall.manager.front.b2bcomm.model.OperatorDO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.CustomerAdapter;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/1/16 19:00
 */
public abstract class BaseRest {
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    private CustomerAdapter customerAdapter;


    protected OperatorDO getLogin() {
        CustDTO user = UserHolder.getUser();
        if (user == null) {
            throw new GmallException(CommonResponseCode.NotLogin);
        }
        CustomerDTO cust = customerAdapter.queryByIdWithCache(user.getCustId());

        OperatorDO opr = new OperatorDO();
        opr.setOperatorId(user.getCustId());
        opr.setPurchaserId(user.getCustId());
        opr.setMainAccountId(user.getCustId());
        opr.setMain(true);
        opr.setNickname(user.getNick());
        opr.setHeadUrl(user.getHead_url());
        opr.setPhone(user.getPhone());
        opr.setType(AccountTypeEnum.PURCHASER_ACCOUNT.getCode());
        opr.setUsername(cust.getUsername());
        return opr;
    }

    protected String buildOperator() {
        CustDTO user = UserHolder.getUser();
        return String.format("%s|%s", user.getCustId(), user.getNick());
    }
}

