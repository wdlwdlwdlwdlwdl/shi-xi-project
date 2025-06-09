package com.aliyun.gts.gmall.manager.biz.adapter;


import com.aliyun.gts.gmall.center.misc.api.dto.input.dict.DictQueryRpcReq;
import com.aliyun.gts.gmall.center.misc.api.dto.output.dict.DictDTO;
import com.aliyun.gts.gmall.center.misc.api.facade.dict.DictReadFacade;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.biz.constant.MiscResponseCode;
import com.aliyun.gts.gmall.manager.biz.converter.MiscConverter;
import com.aliyun.gts.gmall.manager.biz.output.DictVO;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.utils.CacheUtils;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.MISC_CENTER_ERROR;

@Slf4j
@Service
public class DictAdapter {

    @Autowired
    private DictReadFacade dictReadFacade;

    @Autowired
    private DatasourceConfig datasourceConfig;

    @Autowired
    private MiscConverter miscConverter;

    private DubboBuilder builder = DubboBuilder.builder()
            .logger(log).sysCode(MISC_CENTER_ERROR).build();

    private static final Cache<String, ?> dictCache = CacheUtils.defaultLocalCache();

    public DictVO queryByKeyWithCache(String dictKey) {
        return CacheUtils.getNullableQuietly(dictCache, dictKey, () -> queryByKey(dictKey));
    }


    public DictVO queryByKey(String dictKey) {
        DictQueryRpcReq query = new DictQueryRpcReq();
        query.setDictKey(dictKey);

        Function<DictQueryRpcReq, RpcResponse<DictDTO>> func = this::queryByKey;
        DictDTO dict = builder.create()
                .id(DsIdConst.misc_dict_query)
                .queryFunc(func)
                .bizCode(MiscResponseCode.DICT_QUERY_FAIL)
                .query(query);
        return miscConverter.toDictVO(dict);
    }

    private RpcResponse<DictDTO> queryByKey(DictQueryRpcReq query) {
        return dictReadFacade.queryBy(query);
    }
}
