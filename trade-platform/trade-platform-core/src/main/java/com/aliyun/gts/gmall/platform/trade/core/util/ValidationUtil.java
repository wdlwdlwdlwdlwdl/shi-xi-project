package com.aliyun.gts.gmall.platform.trade.core.util;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.collections4.CollectionUtils;

/**
 *
 *
 */
public class ValidationUtil {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    /**
     * 将验证结果转换为异常
     * @param obj
     * @param <T>
     */
    public static <T> void validate(T obj) {
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        violations.forEach(a -> {
            if (a != null) {
                throw new ConstraintViolationException(a.getMessage(), violations);
            }
        });
    }

}
