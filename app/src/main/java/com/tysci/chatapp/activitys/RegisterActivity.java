package com.tysci.chatapp.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.tysci.applibrary.base.BaseActivity;
import com.tysci.applibrary.networks.HttpResponseHandler;
import com.tysci.chatapp.app.AppConfigInfo;
import com.tysci.chatapp.networks.HttpClientApi;
import com.tysci.chatapp.utils.SharedPreferencesUtils;
import com.tysci.chatapp.utils.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/11/11.
 */
public class RegisterActivity extends BaseActivity {
    @Bind(R.id.et_user_account)
    protected EditText etUserAccount;
    @Bind(R.id.et_user_password)
    protected EditText etPassword;
    @Bind(R.id.et_user_comfirm_password)
    protected EditText etComfirmPassword;

    @Override
    protected void setRootContentView() {
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void initViews() {
        this.setTitle("注册");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @OnClick(R.id.bt_register)
    protected void register(){
        final String userName=etUserAccount.getText().toString();
        final String userPassowrd=etPassword.getText().toString();
        String userComfirmPassword=etComfirmPassword.getText().toString();
        if(TextUtils.isEmpty(userName)){
            ToastUtil.toastMsg(this,"用户名不能为空");
            return;
        }

        if(TextUtils.isEmpty(userPassowrd)){
            ToastUtil.toastMsg(this,"请输入密码");
            return;
        }

        if(TextUtils.isEmpty(userComfirmPassword)){
            ToastUtil.toastMsg(this,"请确认密码");
            return;
        }

        if(!userPassowrd.equals(userComfirmPassword)){
            ToastUtil.toastMsg(this,"两次输入的密码不一样");
            return;
        }

        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("注册中...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        HttpClientApi.register(userName, userPassowrd, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                progressDialog.dismiss();
                ToastUtil.toastMsg(RegisterActivity.this,"注册成功");

                Intent intent=new Intent();
                intent.putExtra("userName",userName);
                intent.putExtra("userPassword",userPassowrd);
                setResult(RESULT_OK,intent);
                finish();
            }

            @Override
            public void onFail(VolleyError volleyError) {
                progressDialog.dismiss();
                if(volleyError!=null){
                    System.out.println("VolleyError:"+volleyError.getMessage());
                }
                ToastUtil.toastMsg(RegisterActivity.this, "注册失败");
            }
        });
    }

}
