package action.hdsdk.com.sdk;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import action.hdsdk.com.sdk.dialog.LoginDialog;
import action.hdsdk.com.sdk.dialog.SplashDialog;
import action.hdsdk.com.sdk.http.API;
import action.hdsdk.com.sdk.http.HttpCallback;
import action.hdsdk.com.sdk.http.OkHttpHelper;
import action.hdsdk.com.sdk.listener.InitListener;
import action.hdsdk.com.sdk.listener.LoginListener;
import action.hdsdk.com.sdk.utils.ToastUtils;

/**
 * Created by xk on 2017/6/19.
 */
public class HDSDK {

    static OkHttpHelper mOkHttpHelper = OkHttpHelper.getInstance(); // 网络工具类

    private static boolean sInitSuccess = false; // 初始化是否成功
    private static boolean sLoginSuccess = false; // 登录是否成功

    private HDSDK() {

    }

    public static void initialize(final Activity activity, final InitListener initListener) {

        // 显示 splash
        SplashDialog splashDialog = new SplashDialog(activity);
        splashDialog.show();

        // 请求初始化接口
        mOkHttpHelper.get(API.GAME_SETTING, new HttpCallback(activity, Const.INIT_MSG) {
            @Override
            public void onSuccess(JSONObject json) {
                initCallBack(activity, json, initListener, true);
            }

            @Override
            public void onError(JSONObject json) {
                initCallBack(activity, json, initListener, false);
            }

        });

    }


    public static void doLogin(Activity activity,LoginListener loginListener){
        // 假如还没初始化则不允许登录
        if(!sInitSuccess){
            ToastUtils.showErrorToast(activity, null, Const.ERROR_TIP_LOGIN);
            return;
        }

        // 显示登录对话框
        LoginDialog loginDialog = new LoginDialog(activity,loginListener);
        loginDialog.show();

    }



    /**
     * 初始化成功的回调
     *
     * @param json
     * @param initListener
     * @param isSuccess
     */
    private static void initCallBack(Activity activity, JSONObject json, InitListener initListener, boolean isSuccess) {
        if (isSuccess) {
            try {
                if (json.getString("code").equals("1")) {
                    sInitSuccess = true;
                    initListener.onInitSuccess(json);
                } else {
                    sInitSuccess = false;
                    // 显示错误提示框
                    ToastUtils.showErrorToast(activity, json, null);
                    initListener.onInitFail(json);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            sInitSuccess = false;
            initListener.onInitFail(json);
            // 显示初始化错误的吐司
            ToastUtils.showErrorToast(activity, json, null);
        }
    }




}
