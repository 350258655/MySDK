package action.hdsdk.com.sdk;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

import action.hdsdk.com.sdk.db.BindPhoneUser;
import action.hdsdk.com.sdk.db.PreferencesUtils;
import action.hdsdk.com.sdk.db.UserList;
import action.hdsdk.com.sdk.dialog.ExitDialog;
import action.hdsdk.com.sdk.utils.ToastUtils;

public class UserCenterActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mTvUser;
    private TextView mTvChangeWord;
    private TextView mTvCSCenter;
    private TextView mTvUserBind;
    private Button mBtn_logout;
    private ExitDialog mExitDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        initViews();

        initDatas();

        initEvents();

    }


    /**
     * 初始化事件
     */
    private void initEvents() {
        mTvCSCenter.setOnClickListener(this);
        mTvChangeWord.setOnClickListener(this);
        mTvUserBind.setOnClickListener(this);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBtn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 显示切换帐号对话框
                showLogoutDialog();

            }
        });
    }


    /**
     * 显示切换帐号对话框
     */
    private void showLogoutDialog() {

        mExitDialog = new ExitDialog.Builder(UserCenterActivity.this)
                .setTitle(Const.LOGOUT_DIALOG_TITLE)
                .setContent(Const.LOGOUT_DIALOG_MESSAGE)
                .setPositiveButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logout();
                        mExitDialog.dismiss();
                    }
                })
                .setNegativeButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mExitDialog.dismiss();
                    }
                })
                .build();
        // 显示对话框
        mExitDialog.show();

    }


    /**
     * 注销当前用户，其实就是去掉它的token
     */
    private void logout() {
        // 清除token
        HDApplication.access_token = "";
        PreferencesUtils.putString(HDApplication.getContext(),Const.ACCESS_TOKEN,"");

        // 发送广播
        Intent intent = new Intent();
        intent.setAction(Const.ACTION_LOGOUT);
        sendBroadcast(intent);

        // 退出本Activity
        finish();
    }


    /**
     * 初始化数据
     */
    private void initDatas() {
        String[] user = UserList.getFirstUser(this);
        mTvUser.setText("帐号 ： " + user[0]);

    }


    /**
     * 初始化V
     */
    private void initViews() {
        mToolbar = findViewById(R.id.usercenter_toolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        mTvChangeWord = findViewById(R.id.tv_change_word);
        mTvUser = findViewById(R.id.tv_user);
        mTvCSCenter = findViewById(R.id.tv_cs_center);
        mTvUserBind = findViewById(R.id.tv_user_bind);
        mBtn_logout = findViewById(R.id.btn_logout);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.tv_cs_center){
            startActivity(new Intent(UserCenterActivity.this,CSCenterActivity.class));
        }else if(view.getId() == R.id.tv_change_word){
            startActivity(new Intent(UserCenterActivity.this,ChangePsdActivity.class));
        }else if(view.getId() == R.id.tv_user_bind){
            // 绑定手机
            toBindPhone();
        }
    }

    /**
     * 绑定手机
     */
    private void toBindPhone() {
        String username = UserList.getFirstUser(HDApplication.getContext())[0];
        Map<String,String> bindUserList = BindPhoneUser.getAllBindUser();
        if(bindUserList.containsKey(username)){
            ToastUtils.showErrorToast(HDApplication.getContext(),null,"您已经绑定过手机了！");
        }else {
            // 假如没有绑定手机
            Intent intent = new Intent(UserCenterActivity.this, BindPhoneActivity.class);
            intent.putExtra(Const.CURRENT_USER,username);
            startActivity(intent);
        }

    }
}
