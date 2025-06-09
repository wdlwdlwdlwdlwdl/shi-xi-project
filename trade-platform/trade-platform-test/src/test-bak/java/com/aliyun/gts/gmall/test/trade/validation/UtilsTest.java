package com.aliyun.gts.gmall.test.platform.validation;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.LogisticsInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CountOrderQueryRpcReq;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.validation.annotation.Validated;
import org.testng.collections.Lists;

@Validated
public class UtilsTest {

    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    public static <T> void validate(T obj) {
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        if (CollectionUtils.isNotEmpty(violations)) {
            throw new ConstraintViolationException(violations);
        }
    }

    @Test
    @Ignore
    public void test(){
        CountOrderQueryRpcReq req = new CountOrderQueryRpcReq();
        validate(req);
    }

    @Test
    public void testF(){
        List<String> list = Lists.newArrayList("a","b");
        list.stream().forEach(l->{
            System.out.println(l);
        });
    }

    @Test
    public void testIp() throws UnknownHostException {
        long current = System.currentTimeMillis();
        InetAddress.getLocalHost();
        System.out.println(System.currentTimeMillis() - current);
    }


    @ParameterScriptAssert(script = "arg0.custId != null || arg0.status != null ",lang = "java"
        ,message = "不能同时为空")
    public void testP(CountOrderQueryRpcReq req){
        System.out.println("done");
    }

    @Test
    @Ignore
    public void testMethod() throws NoSuchMethodException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        ExecutableValidator executableValidator = factory.getValidator().forExecutables();
        UtilsTest utilsTest = new UtilsTest();
        Method method  = UtilsTest.class.getMethod("testP",CountOrderQueryRpcReq.class);
        Arrays.stream(method.getAnnotations()).forEach(a->{
            System.out.println(a.annotationType());
        });
        CountOrderQueryRpcReq req = new CountOrderQueryRpcReq();
        //req.setCustId(1L);
        Object[] parameterValues = {req};
        Set<ConstraintViolation<UtilsTest>> violations
            = executableValidator.validateParameters(utilsTest, method, parameterValues);
        if (CollectionUtils.isNotEmpty(violations)) {
            throw new ConstraintViolationException(violations);
        }

        validate(req);
    }

    @Test
    @Ignore
    public void testLogistics(){

        TcLogisticsRpcReq tcLogisticsRpcReq = new TcLogisticsRpcReq();

        LogisticsInfoRpcReq req = new LogisticsInfoRpcReq();
        req.setLogisticsId("xxxx");
        req.setCompanyType(-1);
        tcLogisticsRpcReq.setInfoList(Lists.newArrayList(req));


        tcLogisticsRpcReq.setPrimaryOrderId(1L);
        tcLogisticsRpcReq.setCustId(1L);
        tcLogisticsRpcReq.setSellerId(1L);
        tcLogisticsRpcReq.setReceiverAddr("xx");
        tcLogisticsRpcReq.setReceiverName("xx");
        tcLogisticsRpcReq.setReceiverPhone("xx");
        tcLogisticsRpcReq.setType(1);

        validate(tcLogisticsRpcReq);




    }


}
