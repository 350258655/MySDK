package action.hdsdk.com.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import action.hdsdk.com.sdk.http.API;
import action.hdsdk.com.sdk.http.HttpCallback;
import action.hdsdk.com.sdk.http.OkHttpHelper;
import action.hdsdk.com.sdk.utils.FormVerifyUtils;
import action.hdsdk.com.sdk.utils.ToastUtils;
import action.hdsdk.com.sdk.utils.Utils;

public class BindPhoneActivity extends Activity {

    private Toolbar mToolbar;
    private TextView mTv_currentUser;
    private EditText mEt_phone;
    private Button mBtn_next;
    private String mCurrentUser;
    private OkHttpHelper mOkHttpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        setContentView(R.layout.activity_bind_phone);

        // 初始化View
        initViews();

        // 初始化数据
        initDatas();

        // 初始化事件
        initEvent();

    }

    /**
     * 初始化事件
     */
    private void initEvent() {

        mOkHttpHelper = OkHttpHelper.getInstance();

        // 点击下一步的时候
        mBtn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNum = mEt_phone.getText().toString();
                // 检查手机号码的正确性
                if (FormVerifyUtils.checkMobile(phoneNum)) {
                    String url = API.GAME_VERIFY_CONE + "&phone=" + phoneNum + "&s=" + Const.s + "&access_token=" + HDApplication.access_token;
                    Utils.log(BindPhoneActivity.class, "请求验证码的URL：" + url);
                    mOkHttpHelper.get(url, new HttpCallback(BindPhoneActivity.this, "正在加载") {
                        @Override
                        public void onSuccess(JSONObject json) {
                            dealWithSuccess(json);
                        }

                        @Override
                        public void onError(JSONObject json) {
                            ToastUtils.showErrorToast(BindPhoneActivity.this, json, null);
                        }
                    });

                } else {
                    ToastUtils.showErrorToast(BindPhoneActivity.this, null, "请输入正确的手机号码！");
                }

            }
        });


        // 点击toolbar的返回按钮
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 关闭当前界面
                finish();
            }
        });
    }


    /**
     * 获取验证码成功
     * @param json
     */
    private void dealWithSuccess(JSONObject json) {
        try {
            if(json.getString("code").equals("1")){
                // 跳转到校验验证码的界面
                Intent intent = new Intent(BindPhoneActivity.this,CheckVeryCodeActivity.class);
                intent.putExtra(Const.PHONE,mEt_phone.getText().toString().trim());
                startActivity(intent);

                // 把当前的Activity添加到集合中
                HDApplication.getInstance().addActivity(this);
            }else {
                ToastUtils.showErrorToast(HDApplication.getContext(),json,null);
            }
        } catch (JSONException e) {
            ToastUtils.showErrorToast(HDApplication.getContext(),null,e.getMessage());
        }


    }


    /**
     * 初始化数据
     */
    private void initDatas() {
        // 获取当前用户
        Intent intent = getIntent();
        mCurrentUser = intent.getStringExtra(Const.CURRENT_USER);

        // 设置到界面上
        mTv_currentUser.setText("当前帐号："+mCurrentUser);

    }


    /**
     * 初始化View
     */
    private void initViews() {
        // 获取ToolBar
        mToolbar = findViewById(R.id.bind_toolbar);
        mToolbar.setNavigationIcon(R.drawable.back);

        // 获取其他View
        mTv_currentUser = findViewById(R.id.tv_current_user);
        mEt_phone = findViewById(R.id.et_bind_phone);
        mBtn_next = findViewById(R.id.btn_tonext);
    }
}
