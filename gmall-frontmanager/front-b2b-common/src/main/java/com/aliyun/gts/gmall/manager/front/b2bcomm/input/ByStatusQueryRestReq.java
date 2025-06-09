package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import lombok.Data;

@Data
public class ByStatusQueryRestReq extends ByIdQueryRestReq{

    Integer status;

}
