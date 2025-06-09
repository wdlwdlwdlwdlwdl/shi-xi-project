package com.aliyun.gts.gmall.manager.front.sourcing.vo.pr;

import com.aliyun.gts.gmall.manager.front.sourcing.vo.BaseVO;
import lombok.Data;

import java.util.Date;

@Data
public class PurchaseRequestBaseVO extends BaseVO {

    Long prId;

    String prName;

    String prUser;

    String prDep;

    Date[] prTime;

    Integer status;

    Date gmtCreate;

}
