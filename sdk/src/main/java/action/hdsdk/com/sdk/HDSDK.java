package action.hdsdk.com.sdk;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import action.hdsdk.com.sdk.db.PreferencesUtils;
import action.hdsdk.com.sdk.dialog.AutoLoginDialog;
import action.hdsdk.com.sdk.dialog.LoginDialog;
import action.hdsdk.com.sdk.dialog.OrderDialog;
import action.hdsdk.com.sdk.dialog.SplashDialog;
import action.hdsdk.com.sdk.http.API;
import action.hdsdk.com.sdk.http.HttpCallback;
import action.hdsdk.com.sdk.http.OkHttpHelper;
import action.hdsdk.com.sdk.listener.InitListener;
import action.hdsdk.com.sdk.listener.LoginListener;
import action.hdsdk.com.sdk.listener.PayListener;
import action.hdsdk.com.sdk.utils.ToastUtils;

/**
 * Created by xk on 2017/6/19.
 */
public class HDSDK {

    static OkHttpHelper mOkHttpHelper = OkHttpHelper.getInstance(); // 网络工具类

    private static boolean sInitSuccess = false; // 初始化是否成功
    // private static boolean sLoginSuccess = false; // 登录是否成功

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


    /**
     * 登录
     *
     * @param activity
     * @param loginListener
     */
    public static void doLogin(Activity activity, LoginListener loginListener) {
        // 假如还没初始化则不允许登录
        if (!sInitSuccess) {
            ToastUtils.showErrorToast(activity, null, Const.ERROR_TIP_LOGIN);
            return;
        }

        // 判断是自动登录还是普通登录
        String accessToekn = HDApplication.access_token;
        if (accessToekn == null || accessToekn.equals("")) {
            // 从sp中获取
            accessToekn = PreferencesUtils.getString(activity, Const.ACCESS_TOKEN, "");
            if (accessToekn == null || accessToekn.equals("")) {
                // 显示登录对话框
                LoginDialog loginDialog = new LoginDialog(activity, loginListener);
                loginDialog.show();
            } else {
                // 显示自动登录对话框
                //ToastUtils.showErrorToast(HDApplication.getContext(),null,"显示自动登录对话框");
                AutoLoginDialog autoLoginDialog = new AutoLoginDialog(activity, accessToekn, loginListener);
                autoLoginDialog.show();
            }
        }


        // 显示登录对话框
//        LoginDialog loginDialog = new LoginDialog(activity,loginListener);
//        loginDialog.show();

    }


    /**
     * 支付
     *
     * @param activity
     * @param payListener
     */
    public static void doPay(Activity activity, PayListener payListener) {

        // 假如还没初始化则不允许支付
        if (!sInitSuccess) {
            ToastUtils.showErrorToast(activity, null, Const.ERROR_TIP_PAY);
            return;
        }


        OrderDialog orderDialog = new OrderDialog(activity,payListener,"测试",0.01);
        orderDialog.show();


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
