package com.shake.hdsdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import action.hdsdk.com.sdk.Const;
import action.hdsdk.com.sdk.HDSDK;
import action.hdsdk.com.sdk.UserCenterActivity;
import action.hdsdk.com.sdk.listener.ExitListener;
import action.hdsdk.com.sdk.listener.InitListener;
import action.hdsdk.com.sdk.listener.LoginListener;
import action.hdsdk.com.sdk.listener.LogoutListener;
import action.hdsdk.com.sdk.listener.PayListener;
import action.hdsdk.com.sdk.utils.Utils;

/**
 * 不足的地方：
 *  1、有关那些像素，px，dp等等还不清楚
 *  2、像有些图片什么的，是放在高像素级别的目录，还是低像素的目录。这是屏幕适配的问题。因为对话框的大小，去到不同的手机，就显示不一样了
 *      觉得应该是在那种 value中设置那种dimen吧。这个等基本功能开发完成之后，再来研究总结
 *  3、进度对话框的问题。ProgressDialogUtils
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_init = (Button) findViewById(R.id.btn_init);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        Button btn_order = (Button) findViewById(R.id.btn_order);
        Button btn_user = (Button) findViewById(R.id.btn_exit);

        btn_init.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_order.setOnClickListener(this);
        btn_user.setOnClickListener(this);

        // 设置注销事件
        HDSDK.setLogoutListener(new LogoutListener() {
            @Override
            public void onLogout(String msg) {
                if(msg.equals(Const.LOGOUT_SUCCESS)){
                    Utils.log(MainActivity.class,"用户注销");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_init:
                HDSDK.initialize(this,new CpInitListener());
                break;
            case R.id.btn_login:
                HDSDK.doLogin(this,new CpLoginListener());
                break;
            case R.id.btn_order:
                HDSDK.doPay(this,new CpOrderListener(),"测试",0.01,"http://callback","外部订单","角色ID","区服ID","扩展信息","产品描述");
                break;
            case R.id.btn_exit:
               // startActivity(new Intent(MainActivity.this, UserCenterActivity.class));
                HDSDK.doExit(this,new CpExitListener());
                break;

        }
    }


    private class CpInitListener implements InitListener{

        @Override
        public void onInitSuccess(JSONObject json) {
            Utils.log(MainActivity.class,"初始化成功："+json);
        }

        @Override
        public void onInitFail(JSONObject json) {
            Utils.log(MainActivity.class,"初始化失败："+json);
        }
    }


    private class CpLoginListener implements LoginListener{

        @Override
        public void onLoginSuccess(JSONObject json) {
            Utils.log(MainActivity.class,"登录成功："+json);
        }

        @Override
        public void onLoginFail(JSONObject json) {
            Utils.log(MainActivity.class,"登录失败："+json);
        }
    }

    private class CpOrderListener implements PayListener{

        @Override
        public void onPaySuccess(String msg) {
            if(msg.equals(Const.SUCCESS)){
                Utils.log(MainActivity.class,"支付成功！");
            }
        }

        @Override
        public void onPayCancle(String msg) {
            if(msg.equals(Const.CANCLE)){
                Utils.log(MainActivity.class,"取消支付");
            }
        }

        @Override
        public void onPayFail(String msg) {

            Utils.log(MainActivity.class,"支付失败："+msg);
        }
    }


    private class CpExitListener implements ExitListener{

        @Override
        public void onExitSuccess(String msg) {
            if(msg.equals(Const.EXIT_SUCCESS)){
                Utils.log(MainActivity.class,"退出成功！");
            }
        }

        @Override
        public void onExitCancle(String msg) {
            if(msg.equals(Const.EXIT_CANCLE)){
                Utils.log(MainActivity.class,"取消退出");
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HDSDK.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HDSDK.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HDSDK.onDestroy(this);
    }
}
