package com.aliyun.gts.gmall.platform.trade.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.aliyun.gts.gmall.platform.trade.common.searchconvertor.BaseTypeHandler;
import com.aliyun.gts.gmall.platform.trade.common.searchconvertor.MappingConvertorHandler;

/**
 * @author chaoyin
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchMapping {

    String value() default "";

    Class<? extends MappingConvertorHandler> handler() default BaseTypeHandler.class;

    /**
     * 不处理该字段  处理它的下级字段
     * @return
     */
    boolean mapChild() default false;
}
