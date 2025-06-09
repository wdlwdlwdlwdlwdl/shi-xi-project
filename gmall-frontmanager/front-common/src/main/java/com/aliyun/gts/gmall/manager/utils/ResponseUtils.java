package com.aliyun.gts.gmall.manager.utils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import org.apache.commons.lang.BooleanUtils;

/**
 *
 */
public class ResponseUtils {

    /**
     * Convert from T Page to R Page
     *
     * @param from
     * @param convertFunction 转化函数
     * @param withMsg
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> RestResponse<PageInfo<R>> convertVOPageResponse(RpcResponse<PageInfo<T>> from,
        Function<T, R> convertFunction, Boolean withMsg) {
        if (from.isSuccess()) {
            long total = from.getData().getTotal();
            List<R> list = from.getData().getList().stream().map(convertFunction).collect(Collectors.toList());
            PageInfo<R> r = new PageInfo<>(total, list);
            return BooleanUtils.isTrue(withMsg) ? RestResponse.ok(r) : RestResponse.okWithoutMsg(r);
        } else {
            return RestResponse.fail(from.getFail().getCode(), from.getFail().getMessage());
        }
    }

    public static <T, R> PageInfo<R> convertVOPage(PageInfo<T> from,
                                                   Function<T, R> convertFunction) {
        long total = from.getTotal();
        List<R> list = from.getList().stream().map(convertFunction).collect(Collectors.toList());
        return new PageInfo<>(total, list);
    }

    /**
     * Convert from T List to R List
     *
     * @param from
     * @param convertFunction
     * @param withMsg
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> RestResponse<List<R>> convertVOListResponse(RpcResponse<List<T>> from, Function<T, R> convertFunction,
        Boolean withMsg) {
        if (from.isSuccess()) {
            List<R> list = from.getData().stream().map(convertFunction).collect(Collectors.toList());
            return BooleanUtils.isTrue(withMsg) ? RestResponse.ok(list) : RestResponse.okWithoutMsg(list);
        } else {
            return RestResponse.fail(from.getFail().getCode(), from.getFail().getMessage());
        }
    }

    /**
     * Convert from T to R
     *
     * @param from
     * @param convertFunction 转化函数
     * @param withMsg
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> RestResponse<R> convertVOResponse(RpcResponse<T> from, Function<T, R> convertFunction, Boolean withMsg) {
        if (from.isSuccess()) {
            R r = convertFunction.apply(from.getData());
            return BooleanUtils.isTrue(withMsg) ? RestResponse.ok(r) : RestResponse.okWithoutMsg(r);
        } else {
            return RestResponse.fail(from.getFail().getCode(), from.getFail().getMessage());
        }
    }

    /**
     * @param from
     * @param withMsg
     * @return
     */
    public static RestResponse convertVoidResponse(RpcResponse from, Boolean withMsg) {
        if (from.isSuccess()) {
            return BooleanUtils.isTrue(withMsg) ? RestResponse.OK_VOID : RestResponse.OK_VOID_NO_MSG;
        } else {
            return RestResponse.fail(from.getFail().getCode(), from.getFail().getMessage());
        }
    }

    /**
     * @param from
     * @param withMsg
     * @return
     */
    public static RestResponse<Boolean> operatorResult(RpcResponse<Boolean> from, Boolean withMsg) {
        if (from.isSuccess()) {
            return BooleanUtils.isTrue(withMsg) ? RestResponse.OK_VOID : RestResponse.OK_VOID_NO_MSG;
        } else {
            return RestResponse.fail(from.getFail().getCode(), from.getFail().getMessage());
        }
    }

    /**
     * @param from
     * @return
     */
    public static RestResponse convertResponse(RpcResponse from , Boolean withMsg) {
        if (from.isSuccess()) {
            return RestResponse.ok(from.getData());
        } else {
            return RestResponse.fail(from.getFail().getCode(), from.getFail().getMessage());
        }
    }

}
