package com.aliyun.gts.gmall.manager.front.config;

//import com.aliyun.gts.gmall.manager.front.sourcing.vo.FulfillRequireVO;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;

public class GmallMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
        throws IOException, HttpMessageNotReadableException {

//        if(FulfillRequireVO.accept(type)){
//            return FulfillRequireVO.build(type, inputMessage);
//        }
        return super.read(type, contextClass, inputMessage);
    }

}
