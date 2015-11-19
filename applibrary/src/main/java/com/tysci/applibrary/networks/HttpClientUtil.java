package com.tysci.applibrary.networks;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.TimeUtils;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Http请求工具类
 * Created by Administrator on 2015/11/10.
 *
 */
public class HttpClientUtil {
    /**OkHTTP请求的工具类*/
    private OkHttpClient okHttpClient;

    private Handler handler=null;

    /**请求队列*/
    private RequestQueue requestQueue;

    /**请求标记*/
    private String requestTag="volley";

    private static HttpClientUtil httpClientUtil;

    private String httpCachePath=null;

    public HttpClientUtil(Context context,String httpCachePath){
        System.out.println("创建HttpClientApi...");
        okHttpClient=new OkHttpClient();
        handler=new Handler(Looper.getMainLooper());
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
        sendRequest(Request.Method.GET, url, headers, params, responseHandler);
    }

    public void sendGetRequest(String url,Map<String,String>params,final HttpResponseHandler responseHandler) {
        sendRequest(Request.Method.GET, url, null, params, responseHandler);
    }

    public void sendGetRequest(String url,final HttpResponseHandler responseHandler){
        sendGetRequest(url, null, responseHandler);
    }

    /**
     * 上传文件
     * @param url
     * @param tag
     * @param headers
     * @param params
     * @param files
     * @param httpResultCallback
     */
    public void uploadFiles(String url,String tag,Map<String,String>headers,Map<String,String> params,File[] files, final HttpResultCallback httpResultCallback){
        com.squareup.okhttp.Request.Builder builder=new com.squareup.okhttp.Request.Builder();
        /**添加请求头部*/
        if(headers!=null&&headers.size()>0){
            for(String key:headers.keySet()){
                builder.addHeader(key,headers.get(key));
            }
        }
        MultipartBuilder multipartBuilder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);
        if(params!=null&&params.size()>0){
            for(String key:params.keySet()){
                multipartBuilder.addFormDataPart(key,params.get(key));
            }
        }
        if (files != null&&files.length>0)
        {
            RequestBody fileBody = null;
            for (int i=0;i<files.length;i++){
                String fileKeyName = files[i].getName();
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                multipartBuilder.addPart(Headers.of("Content-Disposition",
                                "form-data; name=\"" + fileKeyName + "\"; filename=\"" + fileName + "\""),
                        fileBody);
//                multipartBuilder.addFormDataPart(fileKeyName,fileName,fileBody);
            }
        }
        com.squareup.okhttp.Request request=builder.url(url)
                                                   .tag(tag)
                                                   .post(new ProgressRequestBody(multipartBuilder.build(), new ProgressRequestBody.ProgressListener() {
                                                       @Override
                                                       public void onRequestProgress(long bytesWritten, long contentLength) {
                                                           final int progress = (int) ((int) (bytesWritten * 100) / contentLength);
                                                           handler.post(new Runnable() {
                                                               @Override
                                                               public void run() {
                                                                   httpResultCallback.loadingProgress(progress);
                                                               }
                                                           });
                                                       }
                                                   }))
                                                   .build();
            OkHttpClient httpClient=okHttpClient.clone();
            httpClient.setRetryOnConnectionFailure(true);
            httpClient.setConnectTimeout(60, TimeUnit.SECONDS);
            httpClient.setWriteTimeout(60, TimeUnit.SECONDS);
            httpClient.setReadTimeout(60, TimeUnit.SECONDS);
            httpClient.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(final com.squareup.okhttp.Request request, final IOException e) {
                handler.post(new Runnable(){
                    @Override
                    public void run(){
                        httpResultCallback.onError(request,e);
                    }
                });
            }
            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                final String responseResult=response.body().string();
                System.out.println("响应体:" + responseResult + " 长度:" + response.body().contentLength());
                System.out.println("响应头:" + response.headers().toString());
                handler.post(new Runnable(){
                    @Override
                    public void run(){
                        httpResultCallback.onSuccess(responseResult);
                    }
                });
            }
        });
    }

    private String getString(InputStream input){
        if(input!=null){
            int i = -1;
            byte[] b = new byte[1024];
            StringBuffer sb = new StringBuffer();
            try {
                while ((i = input.read(b)) != -1) {
                    sb.append(new String(b, 0, i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
        return null;
    }

    private String guessMimeType(String path)
    {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null)
        {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    public void downloadFiles(String url,String tag,Map<String,String> headers,Map<String,String>params,final String saveFile,final HttpResultCallback httpResultCallback){
        com.squareup.okhttp.Request.Builder builder=new com.squareup.okhttp.Request.Builder();
        /**添加请求头部*/
        if (headers!= null&& headers.isEmpty()){
            Headers.Builder headerBuilder = new Headers.Builder();
            for (String key : headers.keySet())
            {
                headerBuilder.add(key, headers.get(key));
            }
            builder.headers(headerBuilder.build());
        }
        /**添加参数*/
        if(params!=null&&!params.isEmpty()){
            StringBuilder sb = new StringBuilder();
            sb.append(url + "?");
            if (params != null && !params.isEmpty())
            {
                for (String key : params.keySet())
                {
                    sb.append(key).append("=").append(params.get(key)).append("&");
                }
            }
            url = sb.deleteCharAt(sb.length() - 1).toString();
        }
        com.squareup.okhttp.Request request=builder.url(url).tag(tag).build();
        OkHttpClient httpClient=okHttpClient.clone();
        httpClient.setRetryOnConnectionFailure(true);
        httpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        httpClient.setWriteTimeout(60, TimeUnit.SECONDS);
        httpClient.setReadTimeout(60, TimeUnit.SECONDS);
        httpClient.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(final com.squareup.okhttp.Request request, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        httpResultCallback.onError(request, e);
                    }
                });
            }
            @Override
            public void onResponse(final com.squareup.okhttp.Response response){
                try {
                    final String filePath=saveDownloadFile(response, saveFile, httpResultCallback);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            httpResultCallback.onSuccess(filePath);
                        }
                    });
                }catch(final IOException exception){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            httpResultCallback.onError(response.request(),exception);
                        }
                    });
                }
            }
        });
    }

    private String saveDownloadFile(com.squareup.okhttp.Response response,String saveFile, final HttpResultCallback httpResultCallback) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[1024];
        int len = 0;
        FileOutputStream fos = null;
        try{
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0;
            File file = new File(saveFile);
            if (!file.exists())
            {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1)
            {
                sum += len;
                fos.write(buf, 0, len);
                if (httpResultCallback != null)
                {
                    final long finalSum = sum;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            httpResultCallback.loadingProgress((int) (finalSum*100/total));
                        }
                    });
                }
            }
            fos.flush();
            return file.getAbsolutePath();
        }finally{
            try
            {
                if (is != null) is.close();
            } catch (IOException e)
            {
            }
            try
            {
                if (fos != null) fos.close();
            } catch (IOException e)
            {
            }
        }
    }

    public interface HttpResultCallback{
        void loadingProgress(int progress);
        void onError(com.squareup.okhttp.Request request,Exception error);
        void onSuccess(String response);
    }

}
