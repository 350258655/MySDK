package action.hdsdk.com.sdk;

import android.content.Intent;
import android.os.CountDownTimer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import action.hdsdk.com.sdk.http.API;
import action.hdsdk.com.sdk.http.HttpCallback;
import action.hdsdk.com.sdk.http.OkHttpHelper;
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
    }

    /**
     * 初始化事件
     */
    private void initEvents() {

        // 去调用服务端接口重新发验证码
        String url = API.CAME_RESET_KEY + "&value="+userName;
        Utils.log(ResetPasswordActivity.class,"resetkey的url："+url);
        mOkHttpHelper.get(url, new HttpCallback(ResetPasswordActivity.this) {
            @Override
            public void onSuccess(JSONObject json) {
                dealWithSuccess(json,Const.RESET_KEY);
            }

            @Override
            public void onError(JSONObject json) {
                ToastUtils.showErrorToast(HDApplication.getContext(),json,null);
            }
        });


        // 重置密码的按钮



    }

    private void dealWithSuccess(JSONObject json, String event) {
        try {
            if(json.getString("code").equals("1")){
                if(event.equals(Const.RESET_KEY)){
                    dealWithResetKeySuccess();
                }
            }
        } catch (JSONException e) {
            ToastUtils.showErrorToast(HDApplication.getContext(),null,e.getMessage());
        }
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
