package com.aliyun.gts.gmall.manager.front.login.adaptor;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.user.api.dto.input.CheckReceivePointQuery;
import com.aliyun.gts.gmall.center.user.api.dto.input.CheckReceivePointCreateReq;
import com.aliyun.gts.gmall.center.user.api.dto.output.CheckReceivePointDTO;
import com.aliyun.gts.gmall.center.user.api.facade.CheckReceivePointReadFacade;
import com.aliyun.gts.gmall.center.user.api.facade.CheckReceivePointWriteFacade;
import com.aliyun.gts.gmall.framework.api.exception.GmallInvalidArgumentException;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.CUSTOMER_CENTER_ERROR;

@Service
@Slf4j
public class CheckReceivePointAdaptor {

    @Autowired
    CheckReceivePointReadFacade checkReceivePointReadFacade;

    @Autowired
    CheckReceivePointWriteFacade checkReceivePointWriteFacade;

    private static final DubboBuilder builder = DubboBuilder.builder().logger(log).sysCode(CUSTOMER_CENTER_ERROR).build();


    public Long save(CheckReceivePointCreateReq checkReceivePointCreateReq) {
        RpcResponse<Long> save = checkReceivePointWriteFacade.save(checkReceivePointCreateReq);
        if(save.isSuccess()){
            return save.getData();
        }
        return null;
    }

    public List<CheckReceivePointDTO> queryByPhone(String phone) {
        CheckReceivePointQuery checkReceivePointQuery = new CheckReceivePointQuery();
        checkReceivePointQuery.setPhone(phone);
        RpcResponse<List<CheckReceivePointDTO>> query = checkReceivePointReadFacade.query(checkReceivePointQuery);
        if(query.isSuccess()&& query.getData()!=null){
            return query.getData();
        }
        else if (!query.isSuccess()){
            throw new GmallInvalidArgumentException(I18NMessageUtils.getMessage("points.judgement.query.fail"));  //# "积分发放判断表信息表查询失败"
        }
        return null;
    }

    public List<CheckReceivePointDTO> queryByPrimary(String custPrimary) {
        CheckReceivePointQuery checkReceivePointQuery = new CheckReceivePointQuery();
        checkReceivePointQuery.setCustPrimary(custPrimary);
        RpcResponse<List<CheckReceivePointDTO>> query = checkReceivePointReadFacade.query(checkReceivePointQuery);
        if(query.isSuccess()&& query.getData()!=null){
            return query.getData();
        }
        else if (!query.isSuccess()){
            throw new GmallInvalidArgumentException(I18NMessageUtils.getMessage("points.judgement.query.fail"));  //# "积分发放判断表信息表查询失败"
        }
        return null;
    }
}
