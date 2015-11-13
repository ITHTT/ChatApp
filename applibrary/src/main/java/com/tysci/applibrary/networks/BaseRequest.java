package com.tysci.applibrary.networks;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/10.
 */
public class BaseRequest extends StringRequest {
    private static final int SOCKET_TIMEOUT=5000;
    private Map<String, String> headers = null;
    private Map<String, String> params = null;

    public BaseRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        initRequest();
    }

    public BaseRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        initRequest();
    }

    private void initRequest(){
        headers = new HashMap<String, String>(1);
        params = new HashMap<String, String>(1);
        this.setRetryPolicy(new DefaultRetryPolicy(SOCKET_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError{
        return headers;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addParam(String key,String value){
        params.put(key, value);
    }

    public void setParams(Map<String,String> params){
        this.params=params;
    }
    public void setHeaders(Map<String,String> headers){
        this.headers=headers;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            String cookie=getCookieValue(response.headers);
            JSONObject dataJson = JSON.parseObject(jsonString);
            if (!TextUtils.isEmpty(cookie)) {
                dataJson.put("cookie", cookie);
            }
            return Response.success(dataJson.toJSONString(),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        super.deliverResponse(response);
    }

    protected String getCookieValue(Map<String,String>headers){
        if(headers!=null&&headers.size()>0){
            String cookies=headers.get("Set-Cookie");
            if(!TextUtils.isEmpty(cookies)){
                String[] cookie=cookies.split(";");
                if(cookie!=null&&cookie.length>0){
                    return cookie[0];
                }
            }
        }
        return null;
    }

}
