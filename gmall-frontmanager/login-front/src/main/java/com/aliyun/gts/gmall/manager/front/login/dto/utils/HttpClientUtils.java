package com.aliyun.gts.gmall.manager.front.login.dto.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http 请求
 *
 * @author tiansong
 */
public class HttpClientUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    private static final HttpClient HTTP_CLIENT  = new HttpClient();
    private static final String     CONTENT_TYPE = "Content-Type";

    public static String sendGet(String url) throws IOException {
        return sendGet(url, null);
    }

    public static String sendGet(String url, Map<String, String> parameters) throws IOException {
        if (url == null) {
            throw new IOException("URL"+ I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
        }
        GetMethod gMethod = createGetMethod(url, parameters);
        return executeMethod(gMethod);
    }

    public static String executeMethod(HttpMethod method) throws IOException {
        HttpConnectionManagerParams httpConnectionManagerParams = HTTP_CLIENT.getHttpConnectionManager().getParams();
        httpConnectionManagerParams.setConnectionTimeout(5000);
        httpConnectionManagerParams.setSoTimeout(5000);

        HTTP_CLIENT.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        int responseCode;
        BufferedReader read=null;
        try {
            responseCode = HTTP_CLIENT.executeMethod(method);
            logger.info("The response code is '{}'", responseCode);
            if (responseCode == HttpStatus.SC_OK) {
                StringBuffer result = new StringBuffer();
                 read = new BufferedReader(
                    new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8"));
                String temp = null;
                while ((temp = read.readLine()) != null) {
                    result.append(temp).append("\r\n");
                }

                return result.toString().trim();
            }
        } catch (IOException e) {
            logger.error("The request url is '" + method.getURI() + "'.", e);
            throw e;
        } finally {
            method.releaseConnection();
            if(read!=null){
                read.close();
            }
        }

        return null;
    }

    private static GetMethod createGetMethod(String url, Map<String, String> parameters) {
        GetMethod gMethod = new GetMethod(url);
        gMethod.setRequestHeader(CONTENT_TYPE, "text/xml;charset=utf-8");

        NameValuePair[] nvps = convert(parameters);
        if (nvps != null) {
            gMethod.setQueryString(nvps);
        }
        return gMethod;
    }

    /**
     * 把集合转成数组
     */
    public static NameValuePair[] convert(Map<String, String> values) {
        NameValuePair[] nvps = null;
        if (values != null && values.size() != 0) {
            Iterator<String> it = values.keySet().iterator();
            nvps = new NameValuePair[values.size()];
            String name = null;
            int i = 0;
            while (it.hasNext()) {
                name = it.next();
                nvps[i] = new NameValuePair(name, values.get(name));
                i++;
            }
        }
        return nvps;
    }
}
