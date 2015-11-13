package com.tysci.applibrary.networks;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.Map;

/**
 * Http请求工具类
 * Created by Administrator on 2015/11/10.
 *
 */
public class HttpClientUtil {
    /**OkHTTP请求的工具类*/
    private OkHttpClient okHttpClient;

    /**请求队列*/
    private RequestQueue requestQueue;

    /**请求标记*/
    private String requestTag="volley";

    private static HttpClientUtil httpClientUtil;

    private String httpCachePath=null;

    public HttpClientUtil(Context context,String httpCachePath){
        System.out.println("创建HttpClientApi...");
        okHttpClient=new OkHttpClient();
        requestQueue=newRequestQueue(context, httpCachePath,okHttpClient);
    }

    public static void initHttpClient(Context context,String httpCache){
        if(httpClientUtil==null){
            httpClientUtil=new HttpClientUtil(context,httpCache);
        }
    }

    public static HttpClientUtil getHttpClientUtil(){
        if(httpClientUtil==null){
            throw new RuntimeException("HttpClientApi没有初始化");
        }
        return httpClientUtil;
    }

    private RequestQueue newRequestQueue(Context context,String cachePath,OkHttpClient okHttpClient){
        File cacheDir = new File(cachePath);
        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        System.out.println("创建RequestQueue...");
        OkHttpStack okHttpStack=new OkHttpStack(okHttpClient);
        Network network = new BasicNetwork(okHttpStack);
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        queue.start();
        return queue;
    }

    /**
     * 设置请求标签
     * @param tag
     */
    public void setRequestTag(String tag){
        this.requestTag=tag;
    }

    /**
     * 关闭相应标签的请求
     * @param tag
     */
    public void cancelRequest(String tag){
        this.requestQueue.cancelAll(tag);
    }

    /**
     * 发送POST请求
     * @param url
     * @param params
     * @param responseHandler
     */
    public void sendRequest(int mothed,String url,Map<String,String>headers,Map<String,String> params, final HttpResponseHandler responseHandler){
        final BaseRequest request=new BaseRequest(
                mothed,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responseHandler.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        responseHandler.onFail(error);
                    }
                }
        );
        if(headers!=null){
            addHeaders(headers,request);
        }
        if(params!=null){
            request.setParams(params);
        }
        request.setTag(requestTag);
        requestQueue.add(request);
    }

    private void addHeaders(Map<String,String>headers,BaseRequest request){
        for(String key:headers.keySet()){
            request.addHeader(key,headers.get(key));
        }
    }

    public void sendPostRequest(String url,Map<String,String>headers,Map<String,String>params,HttpResponseHandler responseHandler){
        sendRequest(Request.Method.POST, url, headers,params, responseHandler);
    }

    public void sendPostRequest(String url,Map<String,String>params,final HttpResponseHandler responseHandler){
        sendRequest(Request.Method.POST, url, null,params, responseHandler);
    }

    public void sendGetRequest(String url,Map<String,String>headers,Map<String,String>params,final HttpResponseHandler responseHandler){
        sendRequest(Request.Method.GET,url,headers,params,responseHandler);
    }

    public void sendGetRequest(String url,Map<String,String>params,final HttpResponseHandler responseHandler){
        sendRequest(Request.Method.GET,url,null,params,responseHandler);
    }

    public void sendGetRequest(String url,final HttpResponseHandler responseHandler){
        sendGetRequest(url,null,responseHandler);
    }

    public void uploadFiles(String url,Map<String,String> params){


    }

    public void downloadFiles(){

    }

    public interface HttpResultCallback{
        void loadingProgress(int progress);
        void onError(Request request,Exception error);
        void onSuccess(String response);
    }

}
