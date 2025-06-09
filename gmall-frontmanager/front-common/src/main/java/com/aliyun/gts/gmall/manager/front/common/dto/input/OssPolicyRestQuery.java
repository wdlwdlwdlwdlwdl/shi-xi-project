package com.aliyun.gts.gmall.manager.front.common.dto.input;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.utils.OssTypeEnum;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * OSS 上传策略获取
 *
 * @author tiansong
 */
@Data
@ApiModel("OSS 上传策略获取")
public class OssPolicyRestQuery extends LoginRestQuery {
    private OssTypeEnum bizType;
    private String fileName;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(bizType, I18NMessageUtils.getMessage("business.type.required"));  //# "业务类型不能为空"
        ParamUtil.nonNull(fileName, I18NMessageUtils.getMessage("file.name.required"));  //# "文件名称不能为空"
    }
}
