package com.shake.hdsdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import action.hdsdk.com.sdk.HDSDK;
import action.hdsdk.com.sdk.listener.InitListener;
import action.hdsdk.com.sdk.listener.LoginListener;
import action.hdsdk.com.sdk.utils.Utils;

/**
 * 不足的地方：
 *  1、有关那些像素，px，dp等等还不清楚
 *  2、像有些图片什么的，是放在高像素级别的目录，还是低像素的目录。这是屏幕适配的问题。因为对话框的大小，去到不同的手机，就显示不一样了
 *      觉得应该是在那种 value中设置那种dimen吧。这个等基本功能开发完成之后，再来研究总结
 *  3、进度对话框的问题。ProgressDialogUtils
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_init = (Button) findViewById(R.id.btn_init);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        Button btn_order = (Button) findViewById(R.id.btn_order);
        Button btn_user = (Button) findViewById(R.id.btn_user);

        btn_init.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_order.setOnClickListener(this);
        btn_user.setOnClickListener(this);
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
                break;
            case R.id.btn_user:
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
}
