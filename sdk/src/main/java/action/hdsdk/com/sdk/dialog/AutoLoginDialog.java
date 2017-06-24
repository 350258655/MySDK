package action.hdsdk.com.sdk.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import action.hdsdk.com.sdk.Const;
import action.hdsdk.com.sdk.HDApplication;
import action.hdsdk.com.sdk.R;
import action.hdsdk.com.sdk.db.PreferencesUtils;
import action.hdsdk.com.sdk.http.API;
import action.hdsdk.com.sdk.http.HttpCallback;
import action.hdsdk.com.sdk.http.OkHttpHelper;
import action.hdsdk.com.sdk.listener.LoginListener;
import action.hdsdk.com.sdk.utils.ToastUtils;
import action.hdsdk.com.sdk.utils.Utils;

/**
 * Created by shake on 2017/6/24 0024.
 * 自动登录对话框
 */
public class AutoLoginDialog extends BaseDialog {

    private String mAccessToken;
    private Context mContext;
    private LoginListener mLoginListener;
    private OkHttpHelper mOkHttpHelper;
    private Button mBtn_logout;
    private boolean isLogout; // 是否要注销用户


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // 如果是要注销，那就不要执行下面的逻辑了
            if(isLogout){
                return;
            }
            try {
                // 此对话框消失
                dismiss();

                // TODO 显示悬浮窗

                // 回调登录成功
                String jsonString = msg.getData().getString(Const.AUTO_LOGIN_CALLBACK);
                mLoginListener.onLoginSuccess(new JSONObject(jsonString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    public AutoLoginDialog(Context context, String accessToken, LoginListener loginListener) {
        super(context, Const.AUTO_LOGIN_DIALOG);
        mAccessToken = accessToken;
        mContext = context;
        mLoginListener = loginListener;
        mOkHttpHelper = OkHttpHelper.getInstance();

        View view = LayoutInflater.from(context).inflate(R.layout.hd_dialog_autologin, null);
        setContentView(view);
        // 初始化View
        initViews();

        // 初始化事件
        initEvent();

    }


    /**
     * 初始化事件
     *
     */
    private void initEvent() {

        // 自动登录
        String url = API.GAME_AUTO_LOGIN + "&access_token=" + mAccessToken+"&s="+Const.s;
        Utils.log(AutoLoginDialog.class, "自动登录的url:" + url);
        mOkHttpHelper.get(url, new HttpCallback(mContext, "登录中") {
            @Override
            public void onSuccess(JSONObject json) {
                dealWithSuccess(json);
            }

            @Override
            public void onError(JSONObject json) {
                ToastUtils.showErrorToast(mContext, json, null);
            }
        });

        // 切换帐号
        mBtn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLogout = true;
                // 对话框消失
                dismiss();
                // 清除token
                HDApplication.access_token = "";
                PreferencesUtils.putString(HDApplication.getContext(),Const.ACCESS_TOKEN,"");

                // 显示登录对话框
                LoginDialog loginDialog = new LoginDialog(mContext,mLoginListener);
                loginDialog.show();

            }
        });

    }


    /**
     * 登录之后
     * TODO 这里自动登录会出问题，怀疑原因是S不能写死
     * @param json
     */
    private void dealWithSuccess(JSONObject json) {
        try {
            if(json.getString("code").equals("1")){

                // 3秒之后再回调成功，并且让对话框消失
                Message message = mHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString(Const.AUTO_LOGIN_CALLBACK,json.toString());
                message.setData(bundle);
                mHandler.sendMessageAtTime(message, SystemClock.uptimeMillis()+2000);

            }else {
                ToastUtils.showErrorToast(HDApplication.getContext(),json,null);
                // 对话框消失
                dismiss();
                // 回调登录失败
                mLoginListener.onLoginFail(json);
            }
        } catch (JSONException e) {
            // 对话框消失
            dismiss();
            ToastUtils.showErrorToast(HDApplication.getContext(),null,e.getMessage());
        }
    }

    private void initViews() {
        mBtn_logout = findViewById(R.id.btn_logout);
    }
}
