package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gcai.platform.sourcing.common.model.PayByDateDO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.PayByOther;
import com.aliyun.gts.gcai.platform.sourcing.common.model.PayByPeriod;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.FulfillRequirement;
import com.aliyun.gts.gcai.platform.sourcing.common.type.FulfillConstants;
import lombok.Data;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 由于springmvc 需要对fulfillRequire里的payTimeLimit做特殊处理
 * 所以vo里用到fulfillRequire的都继承这个类
 */
@Data
public class FulfillRequireVO extends BaseVO{

    FulfillRequirement fulfillRequire;


    public static boolean accept(Type type) {
        return type instanceof Class && FulfillRequireVO.class.isAssignableFrom((Class)type);
    }

    public static <T extends FulfillRequireVO> T build(Type type, HttpInputMessage inputMessage) throws IOException {
        InputStream inputStream = inputMessage.getBody();
        String json = StreamUtils.copyToString(inputStream , StandardCharsets.UTF_8);

        JSONObject jsonObject = JSON.parseObject(json);
        T requireVO = JSON.parseObject(json , type);
        //履约要求
        JSONObject fulfillRequire = jsonObject.getJSONObject("fulfillRequire");
        if(fulfillRequire == null){
            return requireVO;
        }
        //支付账期
        JSONObject payTimeLimit = fulfillRequire.getJSONObject("payTimeLimit");
        if( payTimeLimit == null){
            return requireVO;
        }
        //支付账期类型
        String limitType = payTimeLimit.getString("type");
        if( limitType == null){
            return requireVO;
        }
        String payLimit = payTimeLimit.toJSONString();
        if(FulfillConstants.PAY_TIME_LIMIT_ON_TIME.equals(limitType)){
        }
        if(FulfillConstants.PAY_TIME_LIMIT_BY_PERIOD.equals(limitType)){
            PayByPeriod payByPeriod = JSON.parseObject(payLimit , PayByPeriod.class);
            requireVO.getFulfillRequire().setPayTimeLimit(payByPeriod);
        }
        if(FulfillConstants.PAY_TIME_LIMIT_BY_TIME.equals(limitType)){
            PayByDateDO payByDateDO = JSON.parseObject(payLimit , PayByDateDO.class);
            requireVO.getFulfillRequire().setPayTimeLimit(payByDateDO);
        }
        if(FulfillConstants.PAY_TIME_LIMIT_OTHER.equals(limitType)){
            PayByOther payByOther = JSON.parseObject(payLimit , PayByOther.class);
            requireVO.getFulfillRequire().setPayTimeLimit(payByOther);
        }
        return requireVO;
    }
}
