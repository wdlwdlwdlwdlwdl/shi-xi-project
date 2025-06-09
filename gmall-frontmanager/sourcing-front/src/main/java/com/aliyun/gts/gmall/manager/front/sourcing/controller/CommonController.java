package com.aliyun.gts.gmall.manager.front.sourcing.controller;

import com.aliyun.gts.gmall.manager.front.b2bcomm.controller.BaseRest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/common")
public class CommonController extends BaseRest {
//
//    @Autowired
//    SourcingReadFacade sourcingReadFacade;
//    @Autowired
//    SourcingConvert sourcingConvert;
//    @Autowired
//    AccountReadFacade accountReadFacade;
//
//    @RequestMapping("/get_soucing_head")
//    public RestResponse<SourcingHeadVO> createTemplate(@RequestBody ByIdQueryRestReq req){
//        CommonIdQuery query = new CommonIdQuery();
//        query.setId(req.getId());
//        query.setIncludeDetail(false);
//        RpcResponse<SourcingDTO> sourcingResponse = sourcingReadFacade.queryById(query);
//        if(sourcingResponse.isSuccess()){
//            SourcingDTO sourcingDTO = sourcingResponse.getData();
//            SourcingHeadVO headVO = sourcingConvert.convert2Head(sourcingDTO);
//            return RestResponse.okWithoutMsg(headVO);
//        }
//        return RestResponse.okWithoutMsg(new SourcingHeadVO());
//    }
//
//    @RequestMapping("/select_user")
//    public RestResponse<Set<UserDisplayDO>> selectUser(@RequestBody UserSelectReq req){
//        Set<UserDisplayDO> set = new HashSet<>();
//        AccountPageQueryRequest request = new AccountPageQueryRequest();
//        PageParam param = new PageParam();
//        int pageSize = 100;
//        param.setPageSize(pageSize);
//        request.setPage(param);
//        if(req.getGroupType().equals(UserGroupType.EXPERT.getCode())){
//            request.setType(AccountTypeEnum.EXPERT_ACCOUNT.getCode());
//        }else{
//            request.setType(AccountTypeEnum.PURCHASER_ACCOUNT.getCode());
//        }
//        request.setUserName(req.getUserName());
//        RpcResponse<PageInfo<AccountDTO>>  response = accountReadFacade.queryAccount(request);
//        List<AccountDTO> accountDTOS = response.getData().getList();
//
//        set = accountDTOS.stream().map(a->{
//            UserDisplayDO userDisplayDO = new UserDisplayDO();
//            userDisplayDO.setUserId(a.getId());
//            userDisplayDO.setName(a.getUsername());
//            return userDisplayDO;
//        }).collect(Collectors.toSet());
//
//        return RestResponse.okWithoutMsg(set);
//    }
//
//    @RequestMapping("/ramdon_user")
//    public RestResponse<List<Set<UserDisplayDO>>> selectUser(@RequestBody RandomUserSelectReq req){
//        AccountPageQueryRequest request = new AccountPageQueryRequest();
//        PageParam param = new PageParam();
//        int pageSize = 100;
//        param.setPageSize(pageSize);
//        request.setPage(param);
//        request.setType(AccountTypeEnum.EXPERT_ACCOUNT.getCode());
//        RpcResponse<PageInfo<AccountDTO>>  response = accountReadFacade.queryAccount(request);
//
//        List<AccountDTO> expertList = response.getData().getList();
//        Set<UserDisplayDO> expertSet = new HashSet<>();
//        int randomLimit = expertList.size();
//        Random random = new Random();
//        int index = random.nextInt(randomLimit);
//        while(expertSet.size() < req.getRandomSize() && expertSet.size() < expertList.size()){
//            AccountDTO accountDTO = expertList.get(index);
//            UserDisplayDO userDisplayDO = new UserDisplayDO();
//            userDisplayDO.setName(accountDTO.getUsername());
//            userDisplayDO.setUserId(accountDTO.getId());
//            expertSet.add(userDisplayDO);
//            index = random.nextInt(randomLimit);
//        }
//
//        request.setType(AccountTypeEnum.PURCHASER_ACCOUNT.getCode());
//        response = accountReadFacade.queryAccount(request);
//        List<AccountDTO> purchaserList = response.getData().getList();
//        Set<UserDisplayDO> hostSet = new HashSet<>();
//        Set<UserDisplayDO> guestSet = new HashSet<>();
//        randomLimit = purchaserList.size();
//            //主持人只选一个
//        while(hostSet.size() < 1 && hostSet.size() < purchaserList.size()){
//            index = random.nextInt(randomLimit);
//            AccountDTO accountDTO = purchaserList.get(index);
//            UserDisplayDO userDisplayDO = new UserDisplayDO();
//            userDisplayDO.setName(accountDTO.getUsername());
//            userDisplayDO.setUserId(accountDTO.getId());
//            hostSet.add(userDisplayDO);
//
//        }
//
//        while(guestSet.size() < req.getRandomSize()
//            && guestSet.size() < (purchaserList.size()-hostSet.size())){
//            index = random.nextInt(randomLimit);
//            AccountDTO accountDTO = purchaserList.get(index);
//            UserDisplayDO userDisplayDO = new UserDisplayDO();
//            userDisplayDO.setName(accountDTO.getUsername());
//            userDisplayDO.setUserId(accountDTO.getId());
//            if(hostSet.contains(userDisplayDO)){
//                continue;
//            }
//            guestSet.add(userDisplayDO);
//        }
//
//        List<Set<UserDisplayDO>> result = Lists.newArrayList(expertSet , hostSet , guestSet);
//
//        return RestResponse.okWithoutMsg(result);
//    }

}
