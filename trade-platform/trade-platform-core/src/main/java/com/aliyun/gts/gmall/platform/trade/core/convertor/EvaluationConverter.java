package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.center.trade.api.dto.input.EvaluationDTORpcReq;
import com.aliyun.gts.gmall.middleware.oss.GmallOssClient;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationRateDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationRatePicDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.evaluation.EvaluationRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class EvaluationConverter {

    @Autowired
    private GmallOssClient publicGmallOssClient;
    @Value("${trade.evaluation.toOssUrl:false}")
    private boolean toOssUrl;

    @Mappings({
        @Mapping(target = "rateVideo", source = "rateVideo", qualifiedByName = "httpToOss"),
        @Mapping(target = "ratePic", source = "ratePic", qualifiedByName = "httpToOss"),
    })
    public abstract TcEvaluationDO toTcEvaluationDO(EvaluationRpcReq req);

    public abstract TcEvaluationDO toTcEvaluationDO(EvaluationDTORpcReq req);

    @Mappings({
        @Mapping(target = "rateVideo", source = "rateVideo", qualifiedByName = "ossToHttp"),
        @Mapping(target = "ratePic", source = "ratePic", qualifiedByName = "ossToHttp"),
    })
    public abstract EvaluationDTO toEvaluationDTO(TcEvaluationDO value);

    public abstract List<EvaluationDTO> toEvaluationDTOList(Collection<TcEvaluationDO> value);

    @Named("ossToHttp")
    protected List<String> ossToHttp(List<String> source) {
        return source;
    }

    @Named("httpToOss")
    protected List<String> httpToOss(List<String> source) {
        return source;
    }

    public abstract EvaluationRateDTO toEvaluationDTO(EvaluationRate req);

    public abstract List<EvaluationRatePicDTO> toEvaluationDTOList(List<TcEvaluationDO> list);

    public abstract List<EvaluationDTO> evaluationDTOList(List<TcEvaluationDO> list);
}
