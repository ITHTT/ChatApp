package com.tysci.applibrary.networks;

import com.android.volley.VolleyError;

/**
 * Created by Administrator on 2015/10/8.
 */
public interface HttpResponseHandler {
    void onSuccess(String response);
    void onFail(VolleyError volleyError);
}
