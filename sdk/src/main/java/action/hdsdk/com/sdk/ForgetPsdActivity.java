package action.hdsdk.com.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import action.hdsdk.com.sdk.http.OkHttpHelper;
import action.hdsdk.com.sdk.utils.FormVerifyUtils;
import action.hdsdk.com.sdk.utils.ToastUtils;

public class ForgetPsdActivity extends BaseActivity {

    private Toolbar mToolbar;
    private EditText mEtPhone;
    private Button mBtn_next;
    private EditText mEtUser;
    private OkHttpHelper mOkHttpHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psd);
        // 初始化Views
        initViews();

        // 初始化数据
        initDatas();

        // 初始化事件
        initEvent();

    }


    /**
     * 初始化数据
     */
    private void initDatas() {

        mOkHttpHelper = OkHttpHelper.getInstance();

        // 设置用户名
        Intent intent = getIntent();
        String user = intent.getStringExtra(Const.CURRENT_USER);
        if(user != null && !user.equals("")){
            mEtUser.setText(user);
        }
    }


    /**
     * 初始化事件
     */
    private void initEvent() {
        // 点击下一步
        mBtn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 先检查是不是正确的手机号码
                String phone = mEtPhone.getText().toString().trim();
                boolean isPhone = FormVerifyUtils.checkMobile(phone);
                if(isPhone){
                    // 用户名也不能为空
                    String userName = mEtUser.getText().toString().trim();
                    if(userName == null || userName.equals("")){
                        ToastUtils.showErrorToast(HDApplication.getContext(),null,"用户名不能为空");
                        return;
                    }

                    // 跳转到重置密码界面
                    Intent intent = new Intent(ForgetPsdActivity.this,ResetPasswordActivity.class);
                    intent.putExtra(Const.CURRENT_USER,userName);
                    intent.putExtra(Const.PHONE,phone);
                    startActivity(intent);

                }else {
                    ToastUtils.showErrorToast(HDApplication.getContext(),null,"请输入正确的手机号码！");
                }
            }
        });

        // Toolbar返回
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }




    /**
     * 初始化Views
     */
    private void initViews() {
        mToolbar = findViewById(R.id.forgetpwd_toolbar);
        mEtPhone = findViewById(R.id.et_bind_phone);
        mBtn_next = findViewById(R.id.btn_tonext);
        mEtUser = findViewById(R.id.et_user);
        mToolbar.setNavigationIcon(R.drawable.back);

    }
}
