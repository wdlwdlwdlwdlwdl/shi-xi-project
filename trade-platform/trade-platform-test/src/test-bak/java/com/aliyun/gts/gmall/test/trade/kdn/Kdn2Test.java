package com.aliyun.gts.gmall.test.platform.kdn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.DigestUtils;

public class Kdn2Test {

    public static void main(String[] args) throws IOException {

        //电商ID
        String EBusinessID="test1701397";//请到快递鸟官网申请http://www.kdniao.com/ServiceApply.aspx
        //电商加密私钥，快递鸟提供，注意保管，不要泄漏
        String AppKey="621aba39-83aa-41ed-af88-72ded0561447";//请到快递鸟官网申请http://www.kdniao.com/ServiceApply.aspx
        //请求url
        String ReqURL="http://sandboxapi.kdniao.com:8080/kdniaosandbox/gateway/exterfaceInvoke.json";

        String requestData= "{'OrderCode':'','ShipperCode':'" + "ANE" + "','LogisticCode':'" + "210001633605" + "'}";

        Map<String, String> params = new HashMap<String, String>();
        params.put("RequestData", URLEncoder.encode(requestData, "UTF-8"));
        params.put("EBusinessID", EBusinessID);
        params.put("RequestType", "1002");

        String dataSign= new String(Base64.getEncoder().
            encode(DigestUtils.md5DigestAsHex((requestData+AppKey).getBytes()).getBytes()),"UTF-8");

        params.put("DataSign", URLEncoder.encode(dataSign, "UTF-8"));
        params.put("DataType", "2");


        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(ReqURL);

        List<NameValuePair> nameValuePairs = new ArrayList();
        for(String key : params.keySet()){
            nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
        }

        HttpEntity httpEntity = new UrlEncodedFormEntity(nameValuePairs);

        httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
        httpPost.setEntity(httpEntity);
        CloseableHttpResponse response = httpClient.execute(httpPost);

        HttpEntity entity = response.getEntity();
        String data = EntityUtils.toString(entity);
        System.out.println(data);


    }

}
