package com.aliyun.gts.gmall.manager.front.customer.facade;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginGroupRestQuery;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditCustomerInfoCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditCustomerLanguageCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditHeadUrlCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditPasswordCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CustomerGrowthQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.*;

/**
 * 会员信息的读写
 *
 * @author tiansong
 */
public interface CustomerFacade {

    /**
     * 修改登录密码
     *
     * @param editPasswordCommand
     * @return
     */
    Boolean editPassword(EditPasswordCommand editPasswordCommand);

    /**
     * 编辑用户头像
     *
     * @param editHeadUrlCommand
     * @return
     */
    Boolean editHeadUrl(EditHeadUrlCommand editHeadUrlCommand);

    /**
     * 编辑用户基础信息
     *
     * @param editCustomerInfoCommand
     * @return
     */
    Boolean editBaseInfo(EditCustomerInfoCommand editCustomerInfoCommand);


    /**
     * 编辑用户语言信息
     *
     * @param editCustomerLanguageCommand
     * @return
     */
    RestResponse<String> editLanguageInfo(EditCustomerLanguageCommand editCustomerLanguageCommand);

    /**
     * 查询用户基础信息
     *
     * @param loginRestQuery
     * @return
     */
    CustomerVO queryById(LoginRestQuery loginRestQuery);

    /**
     * 获取用户等级列表
     *
     * @param loginRestQuery
     * @return
     */
    List<CustomerLevelVO> queryLevelList(LoginRestQuery loginRestQuery);

    /**
     * 判断是否新用户
     *
     * @param loginGroupRestQuery
     * @return Boolean
     */
    NewCustomerVO isNewCust(LoginGroupRestQuery loginGroupRestQuery);

    Object testRedis();

    PageInfo<CustomerGrowthRecordVO> queryGrowthRecords(CustomerGrowthQuery customerGrowthQuery);

    List<CustomerGrowthVO> queryCustomerGrowths(CustomerGrowthQuery customerGrowthQuery);
}