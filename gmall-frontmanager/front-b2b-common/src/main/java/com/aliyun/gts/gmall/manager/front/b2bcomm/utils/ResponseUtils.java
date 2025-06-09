package com.aliyun.gts.gmall.manager.front.b2bcomm.utils;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import org.apache.commons.lang.BooleanUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/1/18 20:03
 */
public class ResponseUtils {

    /**
     * copy 一个对象;只返回错误的code
     *
     * @return
     */
    public static <T> RestResponse<T> copyToRest(RpcResponse<T> rsp, Boolean withMsg) {
        if (rsp.isSuccess()) {
            return BooleanUtils.isTrue(withMsg) ? RestResponse.ok(rsp.getData()) : RestResponse.okWithoutMsg(rsp.getData());
        } else {
            return RestResponse.fail(rsp.getFail().getCode(), rsp.getFail().getMessage());
        }
    }

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
    public static RestResponse<Boolean> operateResult(RpcResponse<Boolean> response){
        if(Boolean.TRUE.equals(response.getData() )){
            return RestResponse.ok(response.getData());
        }
        if(response.getFail() != null){
            return  RestResponse.fail("1001",response.getFail().getMessage());
        }
        return RestResponse.fail("1001",I18NMessageUtils.getMessage("operation.fail"));  //# "操作失败"
    }

    public static RestResponse<Boolean> toBoolean(RpcResponse<Long> response){
        if(response.isSuccess() && response.getData() > 0){
            return RestResponse.ok(true);
        }
        if(response.getFail() != null) {
            return RestResponse.fail(response.getFail().getCode(), response.getFail().getMessage());
        }
        return  RestResponse.fail("1001", I18NMessageUtils.getMessage("operation.fail"));  //# "操作失败"
    }
}
