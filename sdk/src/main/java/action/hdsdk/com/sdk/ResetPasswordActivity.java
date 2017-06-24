package action.hdsdk.com.sdk;

import android.content.Intent;
import android.os.CountDownTimer;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import action.hdsdk.com.sdk.BaseActivity;
import action.hdsdk.com.sdk.Const;
import action.hdsdk.com.sdk.HDApplication;
import action.hdsdk.com.sdk.R;
import action.hdsdk.com.sdk.db.PreferencesUtils;
import action.hdsdk.com.sdk.http.API;
import action.hdsdk.com.sdk.http.HttpCallback;
import action.hdsdk.com.sdk.http.OkHttpHelper;
import action.hdsdk.com.sdk.utils.FormVerifyUtils;
import action.hdsdk.com.sdk.utils.ToastUtils;
import action.hdsdk.com.sdk.utils.Utils;

public class ResetPasswordActivity extends BaseActivity {

    private EditText mEtAuthCode;
    private EditText mEtNewPsd;
    private Button mBtnReset;
    private Button mBtnRegetAuthCode;
    private OkHttpHelper mOkHttpHelper;
    private String userName;
    private String phone;
    private CountDownTime mDownTime;
    private Toolbar mToolbar;
    private String access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mOkHttpHelper = OkHttpHelper.getInstance();

        // 初始化Views
        initViews();

        // 初始化数据
        initDatas();

        // 初始化事件
        initEvents();

    }


    /**
     * 初始化数据
     */
    private void initDatas() {
        // 获取手机号码和用户名
        Intent intent = getIntent();
        phone = intent.getStringExtra(Const.PHONE);
        userName = intent.getStringExtra(Const.CURRENT_USER);
        // 初始化时间处理器，总共60秒
        mDownTime = new CountDownTime(60000, 1000);

        // 获取accessToken，假如不存在，证明用户没有登录过，所以就没有access_token，所以就去SP中获取
        access_token = HDApplication.access_token;
        if(access_token == null || access_token.equals("")){
            access_token = PreferencesUtils.getString(HDApplication.getContext(),Const.ACCESS_TOKEN);
        }


    }

    /**
     * 初始化事件
     */
    private void initEvents() {

        // 去调用服务端接口重新发验证码
        String url = API.GAME_RESET_KEY + "&value="+userName;
        Utils.log(ResetPasswordActivity.class, "resetkey的url：" + url);
        mOkHttpHelper.get(url, new HttpCallback(ResetPasswordActivity.this) {
            @Override
            public void onSuccess(JSONObject json) {
                dealWithSuccess(json, Const.RESET_KEY);
            }

            @Override
            public void onError(JSONObject json) {
                ToastUtils.showErrorToast(HDApplication.getContext(),json,null);
            }
        });


        // 重置密码的按钮
        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取验证码和密码
                String authCode = mEtAuthCode.getText().toString().trim();
                String password = mEtNewPsd.getText().toString().trim();

                // 判空处理
                if(authCode == null || authCode.equals("")){
                    ToastUtils.showErrorToast(HDApplication.getContext(),null,"验证码不能为空!");
                    return;
                }

                // 校验密码
                if(FormVerifyUtils.checkUserNamePassword(password)){
                    String url = API.GAME_RESET_PSD+"&value="+phone+"&reset_key="+authCode+"&new_password="+password+"&s="+Const.s+"&access_token="+access_token;
                    Utils.log(ResetPasswordActivity.class,"重置密码的URL："+url);

                    // 校验一下token
                    if(access_token == null || access_token.equals("")){
                        ToastUtils.showErrorToast(HDApplication.getContext(),null,"无法获取游戏token，请联系浩动客服！");
                    }
                    mOkHttpHelper.get(url, new HttpCallback(ResetPasswordActivity.this) {
                        @Override
                        public void onSuccess(JSONObject json) {
                            dealWithSuccess(json,Const.RESET_PSD);
                        }

                        @Override
                        public void onError(JSONObject json) {
                            ToastUtils.showErrorToast(HDApplication.getContext(),json,null);
                        }
                    });

                }else {
                    ToastUtils.showErrorToast(HDApplication.getContext(), null, "请输入正确的密码");
                    return;
                }

            }
        });


        // 重新发送验证码的按钮
        mBtnRegetAuthCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = API.GAME_VERIFY_CONE + "&phone=" + phone + "&s=" + Const.s + "&access_token=" + access_token;
                Utils.log(ResetPasswordActivity.class, "重新获取验证码的URL：" + url);

                // 检查有没有token
                if(access_token == null || access_token.equals("")){
                    ToastUtils.showErrorToast(HDApplication.getContext(),null,"无法获取游戏token，请联系浩动客服！");
                }
                // 重新获取验证码
                mOkHttpHelper.get(url, new HttpCallback(ResetPasswordActivity.this) {
                    @Override
                    public void onSuccess(JSONObject json) {
                        dealWithSuccess(json, Const.REGET_AUTH_CODE);
                    }

                    @Override
                    public void onError(JSONObject json) {
                        ToastUtils.showErrorToast(ResetPasswordActivity.this, json, null);
                    }
                });

            }
        });


        // 点击返回按钮
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void dealWithSuccess(JSONObject json, String event) {
        try {
            if(json.getString("code").equals("1")){
                if(event.equals(Const.RESET_KEY)){
                    dealWithResetKeySuccess();
                }else if(event.equals(Const.RESET_PSD)){
                    dealWithResetPsdSuccess();
                }else if(event.equals(Const.REGET_AUTH_CODE)){
                    // 重新获取验证码
                    dealWithRetGetAuthCodeSuccess();
                }
            }else {
                ToastUtils.showErrorToast(HDApplication.getContext(),json,"");
            }
        } catch (JSONException e) {
            ToastUtils.showErrorToast(HDApplication.getContext(),null,e.getMessage());
        }
    }

    /**
     * 重新发送验证码成功
     */
    private void dealWithRetGetAuthCodeSuccess() {
        // 再重新倒计时
        mDownTime.start();

        // 清空EditText
        mEtAuthCode.setText("");

    }


    /**
     * 重置密码成功后，就应该关掉Activity
     */
    private void dealWithResetPsdSuccess() {
        ToastUtils.showErrorToast(HDApplication.getContext(),null,"重置成功！");
        finish();
        HDApplication.getInstance().finishActivity();
    }

    /**
     * ResetKey成功之后应该发验证码了
     */
    private void dealWithResetKeySuccess() {
        // 开始倒计时
        mDownTime.start();
    }

    /**
     * 初始化Views
     */
    private void initViews() {
        mEtAuthCode = findViewById(R.id.et_auth_code);
        mEtNewPsd = findViewById(R.id.et_newpsd);
        mBtnReset = findViewById(R.id.btn_reset);
        mBtnRegetAuthCode = findViewById(R.id.btn_reget_auth_code);
        mToolbar = findViewById(R.id.resetpwd_toolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
    }


    class CountDownTime extends CountDownTimer {
        //构造函数  第一个参数代表总的计时时长  第二个参数代表计时间隔  单位都是毫秒
        public CountDownTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //每计时一次回调一次该方法
        @Override
        public void onTick(long l) {
            // 设置不可点击
            mBtnRegetAuthCode.setClickable(false);
            // 重新设置
            mBtnRegetAuthCode.setText(l / 1000 + "秒后重新获取");
            mBtnRegetAuthCode.setTextSize(12);
        }

        //计时结束回调该方法
        @Override
        public void onFinish() {
            mBtnRegetAuthCode.setClickable(true);
            mBtnRegetAuthCode.setText("重新获取");
            mBtnRegetAuthCode.setTextSize(16);
        }
    }

}
