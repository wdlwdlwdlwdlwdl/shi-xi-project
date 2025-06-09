package com.aliyun.gts.gmall.manager.front.trade.dto.output;

import lombok.Data;

@Data
public class Result<T> {
    boolean success;
    String errorMsg;
    T data;

    public static <T>Result<T> success(T data){
        Result<T> result = new Result();
        result.success = true;
        result.data = data;
        return result;
    }

    public static <T>Result<T> failed(String msg){
        Result<T> result = new Result();
        result.errorMsg = msg;
        return result;
    }

    public static <T>Result<T> failed(T data, String msg){
        Result<T> result = new Result();
        result.errorMsg = msg;
        result.data = data;
        return result;
    }
}
