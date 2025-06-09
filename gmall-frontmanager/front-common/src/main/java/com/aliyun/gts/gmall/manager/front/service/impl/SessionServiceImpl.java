package com.aliyun.gts.gmall.manager.front.service.impl;

import com.aliyun.gts.gmall.framework.tokenservice.model.Result;
import com.aliyun.gts.gmall.framework.tokenservice.service.TokenService;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.service.SessionService;
import com.aliyun.gts.gmall.manager.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 *
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/11/10 17:29
 */
@Slf4j
@Component
public class SessionServiceImpl implements SessionService {

    @Resource
    private TokenService tokenService;

    @Override
    public CustDTO getUser(HttpServletRequest request) {
        String token = getToken(request);
        if(StringUtils.isEmpty(token)){
            return null;
        }
        //
        Result<Map<String, Object>> result = tokenService.getTokenInfo(token);
        log.info("result返回结果："+result.toString());
        Map<String, Object> custInfo = result.getModel();
        if (custInfo == null || custInfo.isEmpty()) {
            return null;
        }
        String str = JsonUtils.toJSONString(custInfo);
        CustDTO custDTO = JsonUtils.toJavaBean(str, CustDTO.class);
        if(custDTO.getCustId() == null){
            return null;
        }
        return custDTO;
    }

    /**
     * get token
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("X-access-token");
        if(StringUtils.isEmpty(token)){
            token = request.getParameter("token");
        }
        return token;
    }
}
