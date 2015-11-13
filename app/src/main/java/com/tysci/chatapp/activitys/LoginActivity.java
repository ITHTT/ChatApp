package com.tysci.chatapp.activitys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.tysci.applibrary.base.BaseActivity;
import com.tysci.applibrary.networks.HttpClientUtil;
import com.tysci.applibrary.networks.HttpResponseHandler;
import com.tysci.applibrary.utils.ActivitySkipUtils;
import com.tysci.chatapp.app.AppApplication;
import com.tysci.chatapp.app.AppConfigInfo;
import com.tysci.chatapp.networks.HttpClientApi;
import com.tysci.chatapp.networks.HttpUrls;
import com.tysci.chatapp.utils.SharedPreferencesUtils;
import com.tysci.chatapp.utils.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by Administrator on 2015/11/11.
 */
public class LoginActivity extends BaseActivity{
    @Bind(R.id.et_user_account)
    protected EditText etUserAccount;
    @Bind(R.id.et_user_password)
    protected EditText etUserPassword;

    protected ProgressDialog progressDialog=null;

    private final int TOKEN_INCORRECT=0x0001;
    private final int TOKEN_CORRECT=0x0002;
    private final int TOKEN_ERROR=0x0003;

    protected Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            switch(msg.what){
                case TOKEN_CORRECT:
                    //ActivitySkipUtils.skipActivity(LoginActivity.this,MainActivity.class);
                    RongIM.getInstance().startConversation(LoginActivity.this, Conversation.ConversationType.CHATROOM, "1000", "球聊");
                    finish();
                    break;
                case TOKEN_INCORRECT:
                    ToastUtil.toastMsg(LoginActivity.this,"获取的Token不正确");
                    SharedPreferencesUtils.setStringByKey(LoginActivity.this, AppConfigInfo.COOKIE_KEY, null);
                    break;
                case TOKEN_ERROR:
                    ToastUtil.toastMsg(LoginActivity.this,"获取Token错误");
                    break;
            }
        }
    };

    @Override
    protected void setRootContentView() {
        this.setContentView(R.layout.activity_login);

    }

    @Override
    protected void initViews() {
        this.setTitle("登录");
        String account=SharedPreferencesUtils.getStringByKey(this,AppConfigInfo.USER_ACCOUNT);
        if(!TextUtils.isEmpty(account)){
            etUserAccount.setText(account);
            etUserAccount.setSelection(account.length());
        }
        String password=SharedPreferencesUtils.getStringByKey(this,AppConfigInfo.USER_PASSWORD);
        if(!TextUtils.isEmpty(password)){
            etUserPassword.setText(password);
            etUserPassword.setSelection(password.length());
        }
    }

    @OnClick(R.id.bt_login)
    protected void login(){
        final String account=etUserAccount.getText().toString();
        final String password=etUserPassword.getText().toString();

        if(TextUtils.isEmpty(account)){
            ToastUtil.toastMsg(this,"请输入用户名");
            return;
        }

        if(TextUtils.isEmpty(password)){
            ToastUtil.toastMsg(this,"请输入密码");
            return;
        }


        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("登录中....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
        boolean isLogin=false;
        String userName=SharedPreferencesUtils.getStringByKey(this,AppConfigInfo.USER_ACCOUNT);
        String userPassword=SharedPreferencesUtils.getStringByKey(this,AppConfigInfo.USER_PASSWORD);
        if(userName!=null&&userName.equals(account)&&userPassword!=null&&userPassword.equals(password)){
            String cookie=SharedPreferencesUtils.getStringByKey(this,AppConfigInfo.COOKIE_KEY);
            String token=SharedPreferencesUtils.getStringByKey(this,AppConfigInfo.TOKEN_KEY);
            if(!TextUtils.isEmpty(cookie)&&!TextUtils.isEmpty(token)){
                connect(token);
            }else if(TextUtils.isEmpty(token)){
                getToken(cookie);
            }else if(TextUtils.isEmpty(cookie)){
                isLogin=true;
            }
        }else{
            isLogin=true;
        }
       if(isLogin){
           System.out.println("account:" + account);
           HttpClientApi.login(account, password, new HttpResponseHandler() {
               @Override
               public void onSuccess(String response) {
                   System.out.println("响应数据:" + response);
                   if (!TextUtils.isEmpty(response)) {
                       JSONObject json = JSON.parseObject(response);
                       if (json != null && !json.isEmpty()) {
                           String cookie = json.getString("cookie");
                           if (!TextUtils.isEmpty(cookie)) {
                               SharedPreferencesUtils.setStringByKey(LoginActivity.this, AppConfigInfo.COOKIE_KEY, cookie);
                               SharedPreferencesUtils.setStringByKey(LoginActivity.this, AppConfigInfo.USER_ACCOUNT, account);
                               SharedPreferencesUtils.setStringByKey(LoginActivity.this, AppConfigInfo.USER_PASSWORD, password);
                               getToken(cookie);
                               return;
                           }
                       }
                   }
                   progressDialog.dismiss();
                   ToastUtil.toastMsg(LoginActivity.this, "登录失败");
               }

               @Override
               public void onFail(VolleyError volleyError) {
                   progressDialog.dismiss();
                   if (volleyError != null) {
                       System.out.println("volleyError:" + volleyError);
                   }
                   ToastUtil.toastMsg(LoginActivity.this, "登录失败");
               }
           });
       }
    }

    @OnClick(R.id.tv_register)
    protected void gotoRegister(){
        ActivitySkipUtils.skipActivityForResult(this,RegisterActivity.class,AppConfigInfo.REQUEST_CODE_USER_REGISTER,null);
    }

    protected void getToken(String cookie){
        HttpClientApi.getToken(cookie, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                //ToastUtil.toastMsg(LoginActivity.this, "获取Token成功");
                System.out.println("获取Token响应:" + response);
                if (!TextUtils.isEmpty(response)) {
                    JSONObject dataJson = JSON.parseObject(response);
                    if (dataJson != null && !dataJson.isEmpty()) {
                        JSONObject tokenJson = dataJson.getJSONObject("result");
                        if (tokenJson != null) {
                            String token = tokenJson.getString("token");
                            if (!TextUtils.isEmpty(token)) {
                                SharedPreferencesUtils.setStringByKey(LoginActivity.this,AppConfigInfo.TOKEN_KEY,token);
                                connect(token);
                                return;
                            }
                        }
                    }
                }
                progressDialog.dismiss();
                ToastUtil.toastMsg(LoginActivity.this, "获取Token失败");
            }

            @Override
            public void onFail(VolleyError volleyError) {
                progressDialog.dismiss();
                ToastUtil.toastMsg(LoginActivity.this, "获取Token失败");
                SharedPreferencesUtils.setStringByKey(LoginActivity.this,AppConfigInfo.COOKIE_KEY,null);

            }
        });
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {
        if (getApplicationInfo().packageName.equals(AppApplication.getCurProcessName(getApplicationContext()))) {
            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.d("LoginActivity", "--onTokenIncorrect");
                    //ToastUtil.toastMsg(MainActivity.this,"获取的Token不正确");
                    handler.sendEmptyMessage(TOKEN_INCORRECT);
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("MainActivity", "--onSuccess" + " userId: " + userid);
                    //ToastUtil.toastMsg(MainActivity.this,"获取Token成功,用户ID:"+userid);
                    handler.sendEmptyMessage(TOKEN_CORRECT);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d("LoginActivity", "--onError" + errorCode);
                    // ToastUtil.toastMsg(MainActivity.this,"获取Token失败,错误码为:"+errorCode);
                    handler.sendEmptyMessage(TOKEN_ERROR);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==AppConfigInfo.REQUEST_CODE_USER_REGISTER){
            if(data!=null){
                String account=data.getStringExtra("userName");
                String password=data.getStringExtra("userPassword");
                if(!TextUtils.isEmpty(account)){
                    etUserAccount.setText(account);
                    etUserAccount.setSelection(account.length());
                }
                if(!TextUtils.isEmpty(password)){
                    etUserPassword.setText(password);
                    etUserPassword.setSelection(password.length());
                }
                login();
            }
        }
    }
}
