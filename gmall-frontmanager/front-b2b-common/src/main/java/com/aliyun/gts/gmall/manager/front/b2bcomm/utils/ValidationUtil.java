package com.aliyun.gts.gmall.manager.front.b2bcomm.utils;

import javax.validation.*;
import java.util.Set;

/**
 * chaoyin
 */
public class ValidationUtil {

    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    /**
     * 将验证结果转换为异常
     * @param obj
     * @param <T>
     */
    public static <T> void validate(T obj) {
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        if(violations != null && violations.size() > 0){
            throw new ConstraintViolationException(violations);
        }
    }

}
