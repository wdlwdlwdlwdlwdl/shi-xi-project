package com.aliyun.gts.gmall.manager.front.b2bcomm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gshine
 * @since 3/8/21 10:49 AM
 */
//@RestController
//@RequestMapping("/api/dict")
//public class DictRestController extends BaseRest {
//
//    @Autowired
//    private DictService dictService;
//
//    public DictRestController(DictService dictService) {
//        this.dictService = dictService;
//    }
//
//    @RequestMapping("/queryByKey")
//    public RestResponse<DictVO> queryByKey(@RequestBody DictQueryByKeyRestReq req) {
//        return dictService.queryByKey(req.getDictKey());
//    }
//
//    /**
//     * 批量查询
//     * @param req
//     * @return
//     */
//    @RequestMapping("/batchQueryByKey")
//    public RestResponse<Map<String, DictVO>> batchQueryByKey(@RequestBody DictQueryByKeyRestReq req) {
//        ParamUtil.nonNull(req.getDictKeys(), "keys不能为空");
//        List<DictVO> voList = dictService.queryByKey(req.getDictKeys());
//        if (CollectionUtils.isEmpty(voList)) {
//            return RestResponse.okWithoutMsg(new HashMap<>());
//        }
//        Map<String, DictVO> result = voList.stream().collect(Collectors.toMap(en -> en.getDictKey(), en -> en));
//        return RestResponse.okWithoutMsg(result);
//    }
//
//    @RequestMapping("/addDict")
//    public RestResponse<DictVO> addDict(@RequestBody DictAddRestReq req) {
//        req.setCreateId(buildOperator());
//        return dictService.addDict(req);
//    }
//
//    @RequestMapping("/updateDict")
//    public RestResponse<DictVO> updateDict(@RequestBody DictUpdateRestReq req) {
//        req.setUpdateId(buildOperator());
//        return dictService.updateDict(req);
//    }
//
//    @RequestMapping("/deleteDict")
//    public RestResponse<Boolean> deleteDict(@RequestBody ByIdCommandRestReq req) {
//        req.setOperator(buildOperator());
//        return dictService.deleteDict(req);
//    }
//
//    @RequestMapping("/pageQuery")
//    public RestResponse<PageInfo<DictVO>> pageQuery(@RequestBody DictPageQueryRestReq req) {
//        return dictService.pageQuery(req);
//    }

//}
