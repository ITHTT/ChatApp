package com.tysci.applibrary.networks;

import android.util.Pair;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/10.
 */
public class OkHttpUploadRequest {
    /**请求参数*/
    protected Map<String, String> params;
    /**请求头部*/
    protected Map<String, String> headers;

    private Pair<String, File>[] files;


    private void addParams(MultipartBuilder builder, Map<String, String> params)
    {
        if (builder == null)
        {
            throw new IllegalArgumentException("builder can not be null .");
        }

        if (params != null && !params.isEmpty())
        {
            for (String key : params.keySet())
            {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));

            }
        }
    }

    protected void addHeaders(Request.Builder builder, Map<String, String> headers)
    {
        if (builder == null)
        {
            throw new IllegalArgumentException("builder can not be empty!");
        }

        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) return;

        for (String key : headers.keySet())
        {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }


    public RequestBody buildRequestBody()
    {
        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);
        if(params!=null&&params.size()>0) {
            addParams(builder, params);
        }

        if (files != null)
        {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++)
            {
                Pair<String, File> filePair = files[i];
                String fileKeyName = filePair.first;
                File file = filePair.second;
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                builder.addPart(Headers.of("Content-Disposition",
                                "form-data; name=\"" + fileKeyName + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }
        return builder.build();
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

}
