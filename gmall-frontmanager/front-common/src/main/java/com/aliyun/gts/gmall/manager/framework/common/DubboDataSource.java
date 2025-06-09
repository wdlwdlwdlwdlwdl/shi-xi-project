package com.aliyun.gts.gmall.manager.framework.common;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;
import com.aliyun.gts.gmall.framework.api.exception.GmallInvalidArgumentException;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.common.consts.FailCodeConst;

/**
 * 
 * @Title: DubboDataSource.java
 * @Description: Dubbo数据源
 * @author zhao.qi
 * @date 2024年11月14日 10:47:20
 * @version V1.0
 */
public class DubboDataSource extends BaseDataSource {
    private static final String TRADE_DS = "trade_";

    public DubboDataSource(DubboBuilder builder) {
        // 设置了就替换掉默认值,没设置则使用默认值
        if (builder.getStrong() != null) {
            this.strong(builder.getStrong());
        }
        if (builder.getLogger() != null) {
            this.setLogger(builder.getLogger());
        }
        if (builder.getSysCode() != null) {
            this.setSysCode(builder.getSysCode());
        }
        if (builder.getDatasourceConfig() != null) {
            this.setDatasourceConfig(builder.getDatasourceConfig());
        }
    }

    public DubboDataSource id(String id) {
        this.setId(id);
        return this;
    }

    public DubboDataSource strong(boolean strong) {
        this.setStrong(strong);
        return this;
    }

    public DubboDataSource bizCode(ResponseCode code) {
        this.setBizCode(code);
        return this;
    }

    public DubboDataSource queryFunc(Function function) {
        this.setQueryFunc(function);
        return this;
    }

    @Override
    public <T> T query(AbstractRequest request) {
        check();
        return super.query(request, null);
    }

    @Override
    public <T> T query(AbstractRequest request, T defaultValue) {
        check();
        return super.query(request, defaultValue);
    }

    @Override
    public <T> List<T> queryList(AbstractRequest request) {
        check();
        return super.queryList(request, Collections.emptyList());
    }

    @Override
    public <T> List<T> queryList(AbstractRequest request, List<T> defaultValue) {
        check();
        return super.queryList(request, defaultValue);
    }

    @Override
    protected boolean throwRpcCode() {
        // 交易系统的编码默认透出
        return super.throwRpcCode() || this.getId().startsWith(TRADE_DS);
    }

    @Override
    protected boolean blackCode(FailInfo failInfo) {
        return FailCodeConst.isBlackCode(failInfo.getCode());
    }

    @Override
    protected ResponseCode mappingCode(FailInfo failInfo) {
        return FailCodeConst.mappingCode(failInfo.getCode());
    }

    void check() {
        if (StringUtils.isBlank(this.getId()) || DEFAULT_ID.equals(this.getId())) {
            throw new GmallInvalidArgumentException("datasource id " + I18NMessageUtils.getMessage("must.set")); // # 必须设置"
        }
        if (this.getQueryFunc() == null) {
            throw new GmallInvalidArgumentException("query function " + I18NMessageUtils.getMessage("must.set")); // # 必须设置"
        }
    }
}
