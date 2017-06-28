package action.hdsdk.com.sdk;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import action.hdsdk.com.sdk.db.BindPhoneUser;
import action.hdsdk.com.sdk.http.API;
import action.hdsdk.com.sdk.http.HttpCallback;
import action.hdsdk.com.sdk.http.OkHttpHelper;
import action.hdsdk.com.sdk.utils.ToastUtils;
import action.hdsdk.com.sdk.utils.Utils;

public class CheckVeryCodeActivity extends BaseActivity {

    private Toolbar mToolbar;
    private EditText mEtAuthCode;
    private Button btn_regetCode;
    private Button btn_affirm_bind;
    private CountDownTime mDownTime;
    private String phone; // 手机号码
    private OkHttpHelper mOkHttpHelper;
    private String mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // 无title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // 全屏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setContentView(R.layout.activity_check_auth_code);

        // 初始化View
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
        // 获取手机号码
        Intent intent = getIntent();
        phone = intent.getStringExtra(Const.PHONE);
        mCurrentUser = intent.getStringExtra(Const.CURRENT_USER);

        // 初始化网络工具
        mOkHttpHelper = OkHttpHelper.getInstance();
    }


    /**
     * 初始化事件
     */
    private void initEvents() {
        // 回退操作
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 确认按钮
        btn_affirm_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1、过滤验证码
                if (TextUtils.isEmpty(mEtAuthCode.getText().toString().trim())) {
                    ToastUtils.showErrorToast(HDApplication.getContext(), null, "验证码不能为空！");
                    return;
                }

                // 2、检查验证码接口
                String url = API.GAME_CHECK_AUTHCODE + "&resetkey=" + mEtAuthCode.getText().toString().trim() + "&s=" + Const.s + "&access_token=" + HDApplication.access_token;
                Utils.log(CheckVeryCodeActivity.class, "检查验证码的url:" + url);
                mOkHttpHelper.get(url, new HttpCallback(CheckVeryCodeActivity.this) {
                    @Override
                    public void onSuccess(JSONObject json) {
                        dealWithSuccess(json, Const.CHECK_AUTH_CODE);
                    }

                    @Override
                    public void onError(JSONObject json) {
                        ToastUtils.showErrorToast(HDApplication.getContext(), json, null);
                    }
                });


            }
        });


        // 点击重新获取验证码的按钮
        btn_regetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = API.GAME_VERIFY_CONE + "&phone=" + phone + "&s=" + Const.s + "&access_token=" + HDApplication.access_token;
                Utils.log(BindPhoneActivity.class, "重新验证码的URL：" + url);
                // 重新获取验证码
                mOkHttpHelper.get(url, new HttpCallback(CheckVeryCodeActivity.this, "正在加载") {
                    @Override
                    public void onSuccess(JSONObject json) {
                        dealWithSuccess(json, Const.REGET_AUTH_CODE);
                    }

                    @Override
                    public void onError(JSONObject json) {
                        ToastUtils.showErrorToast(CheckVeryCodeActivity.this, json, null);
                    }
                });


            }
        });

    }

    /**
     * 处理绑定手机和验证码的结果
     *
     * @param json
     */
    private void dealWithSuccess(JSONObject json, String type) {

        try {
            if (json.getString("code").equals("1")) {

                if (type.equals(Const.BIND_PHONE)) {
                    // 绑定手机成功
                    dealWithBindSuccess(json);

                } else if (type.equals(Const.CHECK_AUTH_CODE)) {
                    // 检查验证码成功
                    dealWithCheckAuthCodeSuccess(json);
                } else if (type.equals(Const.REGET_AUTH_CODE)) {
                    // 重新获取验证码
                    dealWithRetGetAuthCodeSuccess(json);
                }
            } else {
                ToastUtils.showErrorToast(HDApplication.getContext(), json, null);
            }
        } catch (JSONException e) {
            ToastUtils.showErrorToast(HDApplication.getContext(), null, e.getMessage());
        }


    }


    /**
     * 重新发送验证码成功
     *
     * @param json
     */
    private void dealWithRetGetAuthCodeSuccess(JSONObject json) {
        // 再重新倒计时
        mDownTime.start();

        // 清空EditText
        mEtAuthCode.setText("");
    }

    /**
     * 检查验证码成功
     *
     * @param json
     */
    private void dealWithCheckAuthCodeSuccess(JSONObject json) {
        String url = API.GAME_BIND_PHONE + "&value=" + phone + "&s=" + Const.s + "&access_token=" + HDApplication.access_token;
        Utils.log(CheckVeryCodeActivity.class, "验证手机的接口：" + url);
        mOkHttpHelper.get(url, new HttpCallback(CheckVeryCodeActivity.this, "加载中") {
            @Override
            public void onSuccess(JSONObject json) {
                Utils.log(CheckVeryCodeActivity.class, "绑定手机结果：" + json);
                dealWithSuccess(json, Const.BIND_PHONE);
            }

            @Override
            public void onError(JSONObject json) {
                ToastUtils.showErrorToast(HDApplication.getContext(), json, null);
            }
        });
    }

    /**
     * 绑定手机成功
     *
     * @param json
     */
    private void dealWithBindSuccess(JSONObject json) {

        ToastUtils.showErrorToast(HDApplication.getContext(), null, "绑定手机成功！");
        // 添加到集合中
        BindPhoneUser.addBindUser(mCurrentUser, phone);
        // 关闭当前Activity，和绑定手机界面的那个Activity
        finish();
        HDApplication.getInstance().finishActivity();

    }

    /**
     * 初始化View
     */
    private void initViews() {
        mToolbar = findViewById(R.id.check_code_toolbar);
        mEtAuthCode = findViewById(R.id.et_auth_code);
        btn_regetCode = findViewById(R.id.btn_reget_auth_code);
        btn_affirm_bind = findViewById(R.id.btn_affirm_bind);
        mToolbar.setNavigationIcon(R.drawable.back);
        // 初始化时间处理器，总共60秒
        mDownTime = new CountDownTime(60000, 1000);
        // 开始倒计时
        mDownTime.start();
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
            btn_regetCode.setClickable(false);
            // 重新设置
            btn_regetCode.setText(l / 1000 + "秒后重新获取");
            btn_regetCode.setTextSize(12);
        }

        //计时结束回调该方法
        @Override
        public void onFinish() {
            btn_regetCode.setClickable(true);
            btn_regetCode.setText("重新获取");
            btn_regetCode.setTextSize(16);
        }
    }


}
