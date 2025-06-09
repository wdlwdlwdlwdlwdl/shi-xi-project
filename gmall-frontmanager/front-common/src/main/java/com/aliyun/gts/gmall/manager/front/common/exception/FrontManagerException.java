package com.aliyun.gts.gmall.manager.front.common.exception;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;

/**
 * 前台应用的业务Exception
 *
 * @author tiansong
 */
public class FrontManagerException extends GmallException {
    public FrontManagerException(ResponseCode code, Object... codeMessageArgs) {
        super(code, codeMessageArgs);
    }
}
