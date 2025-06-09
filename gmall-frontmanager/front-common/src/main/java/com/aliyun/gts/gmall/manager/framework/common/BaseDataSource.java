package com.aliyun.gts.gmall.manager.framework.common;

import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Title: BaseDataSource.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2024年11月14日 10:40:51
 * @version V1.0
 */
@Slf4j
@Data
public abstract class BaseDataSource implements DataSource {
    protected static final String DEFAULT_ID = "domain_module_[subModule]_methodName";

    /**
     * 构造方法
     */
    public BaseDataSource() {
        this(DEFAULT_ID, null);
    }

    /**
     * 构造方法
     * 
     * @param id
     * @param queryFunc
     */
    public BaseDataSource(String id, Function<AbstractRequest, RpcResponse> queryFunc) {
        this.id = id;
        this.queryFunc = queryFunc;
        this.strong = Boolean.TRUE;
        this.logger = log;
        this.bizCode = CommonResponseCode.ServerError;
        this.sysCode = CommonResponseCode.ServerBusy;
    }

    /** 数据源唯一标识 建议命名方式：领域名称-模块名称-接口方法名称，如：trade-cart-add, item-detail-getById */
    private String id;

    /** 默认：true强依赖 */
    private Boolean strong;

    /** 日志打印 */
    private Logger logger;

    /** 强依赖调用失败，抛出的异常码 bizCode : 业务场景的异常码 sysCode : 远程服务的异常码 */
    private ResponseCode bizCode;
    private ResponseCode sysCode;

    /** 请求方法 */
    private Function<AbstractRequest, RpcResponse> queryFunc;

    /** 失败后是否抛出rpc错误信息 */
    private boolean failThrow;

    /** 降级配置信息 */
    private DatasourceConfig datasourceConfig;

    @Override
    public <T> T query(AbstractRequest request, T defaultValue) {
        try {
            // 数据源降级
            if (this.degradation()) {
                logger.warn("degradation datasource id= " + this.id);
                return null;
            }
            RpcResponse<T> rpcResponse = this.queryFunc.apply(request);
            if (rpcResponse.isSuccess()) {
                logger.info(this.id + " success, request={}, response={}", request, rpcResponse);
                return rpcResponse.getData();
            }
            logger.warn(this.id + " failed,request={}, msg={}", rpcResponse.getFail());
            this.processBizCode(rpcResponse.getFail());
        } catch (Exception e) {
            logger.error(this.id + " error:", e);
            // 系统级别ERROR
            if (strong) {
                throw new GmallException(sysCode);
            }
        }
        // 业务级别ERROR
        if (strong) {
            throw new GmallException(bizCode);
        }
        return defaultValue;
    }

    @Override
    public <T> List<T> queryList(AbstractRequest request, List<T> defaultValue) {
        try {
            // 数据源降级
            if (this.degradation()) {
                logger.warn("degradation datasource id= " + this.id);
                return null;
            }
            RpcResponse<List<T>> rpcResponse = this.queryFunc.apply(request);
            if (rpcResponse.isSuccess()) {
                logger.info(this.id + " success, request={}, response={}", request, rpcResponse);
                return rpcResponse.getData();
            }
            logger.warn(this.id + " failed,request={}, msg={}", rpcResponse.getFail());
            this.processBizCode(rpcResponse.getFail());
        } catch (Exception e) {
            logger.error(this.id + " error:", e);
            // 系统级别ERROR
            if (strong) {
                throw new GmallException(sysCode);
            }
        }
        // 业务级别ERROR
        if (strong) {
            throw new GmallException(bizCode);
        }
        return defaultValue;
    }

    // ---------------------------------------------------------------------------------------------------------------------
    protected void processBizCode(FailInfo failInfo) {
        // 指定不抛出的 errCode(场景code代替)
        if (this.blackCode(failInfo)) {
            // 无需映射code，使用默认即可
            return;
        }

        // 指定需要映射转换的 errCode
        ResponseCode mappingCode = this.mappingCode(failInfo);
        if (mappingCode != null) {
            // rpc code 和业务code进行转换
            bizCode = mappingCode;
            return;
        }

        // 以上都未匹配, errCode 默认策略
        if (this.throwRpcCode()) {
            bizCode = new FrontResponseCode(failInfo);
            return;
        }
    }

    protected ResponseCode mappingCode(FailInfo failInfo) {
        return null;
    }

    protected boolean blackCode(FailInfo failInfo) {
        return false;
    }

    protected boolean throwRpcCode() {
        return datasourceConfig != null && datasourceConfig.getThrowRpcCode();
    }

    private boolean degradation() {
        return datasourceConfig != null && datasourceConfig.getPerfDsIdSet().contains(id);
    }
}
