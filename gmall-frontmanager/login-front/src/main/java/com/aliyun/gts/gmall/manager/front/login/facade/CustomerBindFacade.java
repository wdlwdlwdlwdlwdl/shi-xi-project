package com.aliyun.gts.gmall.manager.front.login.facade;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.BindCustomerQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.BindCustomerReq;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.UnbindCustomerReq;
import com.aliyun.gts.gmall.manager.front.login.dto.output.BindCustomerResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.PhoneLoginResult;

import java.util.List;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月22日 15:07:00
 */
public interface CustomerBindFacade {

    PhoneLoginResult bindCustomerRelation(BindCustomerReq bindCustomerReq);

    RestResponse<Boolean> unbindCustomerRelation(UnbindCustomerReq unbindCustomerReq);

    List<BindCustomerResult> queryCustomerLoginAccount(BindCustomerQuery bindCustomerQuery);

}
