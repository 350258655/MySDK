package action.hdsdk.com.sdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import action.hdsdk.com.sdk.db.PreferencesUtils;
import action.hdsdk.com.sdk.db.UserList;
import action.hdsdk.com.sdk.http.API;
import action.hdsdk.com.sdk.http.HttpCallback;
import action.hdsdk.com.sdk.http.OkHttpHelper;
import action.hdsdk.com.sdk.utils.FormVerifyUtils;
import action.hdsdk.com.sdk.utils.ToastUtils;
import action.hdsdk.com.sdk.utils.Utils;

public class ChangePsdActivity extends BaseActivity {

    private TextView tv_user;
    private EditText mEtOldPsd;
    private EditText mEtNewPsd;
    private Button mBtnReset;
    private String mUserName;
    private String mPassword;
    private Toolbar mToolbar;
    private OkHttpHelper mOkHttpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_psd);

        initViews();

        initDatas();

        initEvents();
    }

    private void initViews() {
        tv_user = findViewById(R.id.tv_user);
        mEtOldPsd = findViewById(R.id.et_oldpsd);
        mEtNewPsd = findViewById(R.id.et_newpsd);
        mBtnReset = findViewById(R.id.btn_reset);
        mToolbar = findViewById(R.id.resetpwd_toolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        mOkHttpHelper = OkHttpHelper.getInstance();
    }

    private void initEvents() {

        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 重置密码
                retsetPsd();
            }
        });


        // 点击返回
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    /**
     * 重置密码
     */
    private void retsetPsd() {

        // 重置密码
        String oldPsd = mEtOldPsd.getText().toString().trim();
        String newPsd = mEtNewPsd.getText().toString().trim();
        String access_token = HDApplication.access_token;
        if(access_token == null || access_token.equals("")){
            access_token = PreferencesUtils.getString(HDApplication.getContext(),Const.ACCESS_TOKEN);
        }

        if(FormVerifyUtils.checkUserNamePassword(newPsd) && oldPsd.length() > 0){
            String url = API.GAME_UPDATE_PSD + "&old_password="+oldPsd+"&new_password="+newPsd+"&s="+Const.s+"&access_token="+access_token;
            mOkHttpHelper.get(url, new HttpCallback(ChangePsdActivity.this) {
                @Override
                public void onSuccess(JSONObject json) {
                    dealWithSuccess(json);
                }

                @Override
                public void onError(JSONObject json) {
                    ToastUtils.showErrorToast(HDApplication.getContext(),json,null);
                }
            });

        }else {
            ToastUtils.showErrorToast(HDApplication.getContext(),null,"请输入正确格式的密码!");
        }


    }

    private void dealWithSuccess(JSONObject json) {
        try {
            if(json.getString("code").equals("1")){

                Utils.log(ChangePsdActivity.class, "重置密码成功返回：" + json);

                ToastUtils.showErrorToast(HDApplication.getContext(),null,"密码修改成功！");

                // 清除token
                HDApplication.access_token = "";
                PreferencesUtils.putString(HDApplication.getContext(),Const.ACCESS_TOKEN,"");

                // 更新用户的密码
                String[] userData = new String[]{mUserName,mEtNewPsd.getText().toString().trim()};
                UserList.updateUser(userData,HDApplication.getContext());

                finish();

            }else {
                ToastUtils.showErrorToast(HDApplication.getContext(),json,null);
            }
        } catch (JSONException e) {
            ToastUtils.showErrorToast(HDApplication.getContext(),null,e.getMessage());
        }
    }

    private void initDatas() {
        String[] userDatas = UserList.getFirstUser(ChangePsdActivity.this);
        mUserName = userDatas[0];
        mPassword = userDatas[1];
        tv_user.setText("当前帐号 ： "+mUserName);
    }
}
