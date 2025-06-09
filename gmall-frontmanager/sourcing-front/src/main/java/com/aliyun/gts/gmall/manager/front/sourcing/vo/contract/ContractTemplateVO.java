package com.aliyun.gts.gmall.manager.front.sourcing.vo.contract;

import com.aliyun.gts.gcai.platform.contract.common.model.inner.ContractTemplateFeature;
import com.aliyun.gts.gmall.manager.front.b2bcomm.model.FileVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("合同模板")
public class ContractTemplateVO {

    Long id;

    String uploaderName;

    String templateName;

    String description;

    FileVO templateFile;

    List<String> templateVariables;

    String eTemplateId;

    ContractTemplateFeature feature;

}
