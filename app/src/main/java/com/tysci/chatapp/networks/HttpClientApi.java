package com.tysci.chatapp.networks;

import com.tysci.applibrary.networks.HttpClientUtil;
import com.tysci.applibrary.networks.HttpResponseHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/12.
 */
public class HttpClientApi {
    public static final String HOST_URL="http://webim.demo.rong.io/";
    public static final String USER_LOGIN_URL=HOST_URL+"email_login";
    public static final String USER_REGISTER_URL=HOST_URL+"reg";
    public static final String USER_GET_TOKEN=HOST_URL+"token";

    public static final String GET_TOKEN="http://192.168.1.150:8080/Tinfo_ChatServer/user/getToken?id=test";

    public static final String GET_TOKEN1="http://192.168.1.176:8080/chat-server/user/";

    public static final String UPLOAD_FILE_URL="http://192.168.1.176:8080/chat-server/servlet/fileupload";

    public static final boolean IS_LOCAL=false;

    /**
     * 用户登录
     * @param userName
     * @param password
     * @param httpResponseHandler
     */
    public static void login(String userName,String password,HttpResponseHandler httpResponseHandler){
        Map<String,String> params=new HashMap<String,String>(2);
        params.put("email",userName);
        params.put("password",password);
        HttpClientUtil.getHttpClientUtil().sendPostRequest(USER_LOGIN_URL,params,httpResponseHandler);
    }

    public static void register(String userName,String password,HttpResponseHandler httpResponseHandler){
        Map<String,String> params=new HashMap<String,String>(2);
        params.put("email",userName);
        params.put("password",password);
        params.put("mobile","123");
        params.put("username","xxx");
        HttpClientUtil.getHttpClientUtil().sendPostRequest(USER_REGISTER_URL,params,httpResponseHandler);
    }

    public static void getToken(String cookie,HttpResponseHandler httpResponseHandler){
        System.out.println("cookie:"+cookie);
        Map<String,String>headers=new HashMap<>(1);
        headers.put("cookie",cookie);
        HttpClientUtil.getHttpClientUtil().sendGetRequest(USER_GET_TOKEN,headers,null,httpResponseHandler);
    }

    public static void getLocalToken(String userName,HttpResponseHandler httpResponseHandler){
        String url=GET_TOKEN1+userName+"/getToken";
        HttpClientUtil.getHttpClientUtil().sendGetRequest(url,httpResponseHandler);
    }
}
