package com.aliyun.gts.gmall.manager.front.sourcing.vo;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.server.util.DateTimeUtils;
import com.aliyun.gts.gmall.manager.front.b2bcomm.model.KVVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jodd.util.collection.SortedArrayList;
import lombok.Data;

import java.util.Comparator;
import java.util.Date;

@Data
@ApiModel("管理页面公共头部信息")
public class SourcingHeadVO {

    @ApiModelProperty("寻源单名称")
    String title;
    @ApiModelProperty("寻源单id")
    Long id;
    @ApiModelProperty("寻源单状态进度")
    Integer step;
    @ApiModelProperty("寻源单状态")
    Integer status;
    @ApiModelProperty("寻源单个阶段时间设置")
    SortedArrayList<KVVO<String , String>> timeList;

    public void addTimes(String timeName , Date start , Date end){
        String timeStr = DateTimeUtils.format(start) + I18NMessageUtils.getMessage("to") + DateTimeUtils.format(end);  //# "至"
        timeList.add(new KVVO<String , String>(timeName , timeStr));
    }

    public SourcingHeadVO(){
        Comparator<KVVO<String , String>> comparator = new Comparator<KVVO<String, String>>() {
            @Override
            public int compare(KVVO<String, String> o1, KVVO<String, String> o2) {

                return o1.getValue().compareTo(o2.getValue());
            }
        };
        timeList = new SortedArrayList<>(comparator);
    }

    public static void main(String[] args) {
        System.out.println("2020.02.11 00:14:00".compareTo("2020.02.11 00:13:10"));
    }
}
