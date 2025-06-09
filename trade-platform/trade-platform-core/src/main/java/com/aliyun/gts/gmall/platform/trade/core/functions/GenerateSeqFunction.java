package com.aliyun.gts.gmall.platform.trade.core.functions;

/**
 * @author xinchen
 *
 * @param <FROM>
 * @param <TO>
 */
@FunctionalInterface
public interface GenerateSeqFunction<FROM, TO> {

    TO convert(FROM seed);

}
