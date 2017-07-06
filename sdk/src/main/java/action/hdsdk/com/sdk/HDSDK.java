package action.hdsdk.com.sdk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import action.hdsdk.com.sdk.db.PreferencesUtils;
import action.hdsdk.com.sdk.dialog.AutoLoginDialog;
import action.hdsdk.com.sdk.dialog.ExitDialog;
import action.hdsdk.com.sdk.dialog.LoginDialog;
import action.hdsdk.com.sdk.dialog.SplashDialog;
import action.hdsdk.com.sdk.http.API;
import action.hdsdk.com.sdk.http.HttpCallback;
import action.hdsdk.com.sdk.http.OkHttpHelper;
import action.hdsdk.com.sdk.listener.ExitListener;
import action.hdsdk.com.sdk.listener.InitListener;
import action.hdsdk.com.sdk.listener.LoginListener;
import action.hdsdk.com.sdk.listener.LogoutListener;
import action.hdsdk.com.sdk.listener.PayListener;
import action.hdsdk.com.sdk.utils.ToastUtils;
import action.hdsdk.com.sdk.widget.FloatViewService;

/**
 * Created by xk on 2017/6/19.
 */
public class HDSDK {

    static OkHttpHelper mOkHttpHelper = OkHttpHelper.getInstance(); // 网络工具类

    private static FloatViewService mFloatViewService;
    private static IsLoginReceiver mIsLoginReceiver; // 登录是否成功的广播接收者
    private static LogoutReceiver sLogoutReceiver;  // 注销是否成功的广播接收者
    private static PayResultReceiver sPayResultReceiver; // 支付是否成功的广播接收者
    private static LogoutListener sLogoutListener; // 注销的监听器
    private static PayListener sPayListener; // 支付结果的监听器

    private static boolean sInitSuccess = false; // 初始化是否成功
    private static boolean sLoginSuccess = false; // 登录是否成功

    private static ExitDialog sExitDialog; // 退出对话框
    private HDSDK() {

    }

    public static void initialize(final Activity activity, final InitListener initListener) {


        // 显示 splash
        SplashDialog splashDialog = new SplashDialog(activity);
        splashDialog.show();

        // 绑定服务
        activity.bindService(new Intent(activity, FloatViewService.class), mConnection, Context.BIND_AUTO_CREATE);

        // 注册广播接收者检测是否登录
        mIsLoginReceiver = new IsLoginReceiver();
        activity.registerReceiver(mIsLoginReceiver, new IntentFilter(Const.ACTION_LOGIN_STATE));

        // 注册广播接收者检测是否注销
        sLogoutReceiver = new LogoutReceiver();
        activity.registerReceiver(sLogoutReceiver, new IntentFilter(Const.ACTION_LOGOUT));

        // 注册广播接收者检查支付是否成功
        sPayResultReceiver = new PayResultReceiver();
        activity.registerReceiver(sPayResultReceiver,new IntentFilter(Const.ACTION_PAY));



        // gamesetting 接口不接也没什么的，那个只是支付配置
        // 请求初始化接口
//        mOkHttpHelper.get(API.GAME_SETTING, new HttpCallback(activity, Const.INIT_MSG) {
//            @Override
//            public void onSuccess(JSONObject json) {
//                initCallBack(activity, json, initListener, true);
//            }
//
//            @Override
//            public void onError(JSONObject json) {
//                initCallBack(activity, json, initListener, false);
//            }
//
//        });
        // 回调
        initCallBack(activity,initListener,true);


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

        if (sLoginSuccess) {
            ToastUtils.showErrorToast(activity, null, Const.ERROR_HAS_LOGIN);
            return;
        }


        // 判断是自动登录还是普通登录
        String accessToekn = HDApplication.access_token;
        if (accessToekn == null || accessToekn.equals("")) {
            // 从sp中获取
            accessToekn = PreferencesUtils.getString(activity, Const.ACCESS_TOKEN, "");
            if (accessToekn == null || accessToekn.equals("")) {
                // 显示登录对话框
                LoginDialog loginDialog = new LoginDialog(activity, loginListener, mFloatViewService);
                loginDialog.show();
            } else {
                // 显示自动登录对话框
                //ToastUtils.showErrorToast(HDApplication.getContext(),null,"显示自动登录对话框");
                AutoLoginDialog autoLoginDialog = new AutoLoginDialog(activity, accessToekn, loginListener, mFloatViewService);
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
    public static void doPay(Activity activity, PayListener payListener, String productName, double amount, String notifyUrl, String exOrderNum, String roleId, String serverId, String exInfo, String productInfo) {

        // 假如还没登录则不允许支付
        if (!sLoginSuccess) {
            ToastUtils.showErrorToast(activity, null, Const.ERROR_TIP_PAY);
            return;
        }

        // 下单的监听器
        sPayListener = payListener;

//        OrderDialog orderDialog = new OrderDialog(activity, payListener, productName, amount, notifyUrl, exOrderNum, roleId, serverId, exInfo, productInfo);
//        orderDialog.show();

        // 跳转到下单的Activity
        Intent intent = new Intent(activity,OrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Const.ORDER_PRODUCT_NAME,productName);
        bundle.putDouble(Const.ORDER_AMOUNT, amount);
        bundle.putString(Const.ORDER_NOTIFYURL, notifyUrl);
        bundle.putString(Const.ORDER_EX_ORDERNUM, exOrderNum);
        bundle.putString(Const.ORDER_ROLEID, roleId);
        bundle.putString(Const.ORDER_SERVERID, serverId);
        bundle.putString(Const.ORDER_EX_INFO, exInfo);
        bundle.putString(Const.ORDER_PROCUCT_INFO,productInfo);
        intent.putExtra(Const.ORDER_INFO,bundle);
        activity.startActivity(intent);

    }



    public static void doExit(Activity activity, final ExitListener listener){
        sExitDialog = new ExitDialog.Builder(activity)
                .setTitle(Const.EXIT_DIALOG_TITLE)
                .setContent(Const.EXIT_DIALOG_MESSAGE)
                .setPositiveButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onExitSuccess(Const.EXIT_SUCCESS);
                        sExitDialog.dismiss();
                    }
                })
                .setNegativeButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onExitCancle(Const.EXIT_CANCLE);
                        sExitDialog.dismiss();
                    }
                })
                .build();

        // 显示对话框
        sExitDialog.show();

    }



    /**
     * 设置切换帐号的监听器
     *
     * @param listener
     */
    public static void setLogoutListener(LogoutListener listener) {
        sLogoutListener = listener;
    }

    public static void onResume(Activity activity) {
        if (sLoginSuccess) {
            mFloatViewService.showFloatView();
        }
    }

    public static void onPause(Activity activity) {
        if (sLoginSuccess) {
            mFloatViewService.hideFloatView();
        }
    }

    public static void onDestroy(Activity activity) {
        // 注销广播接收者
        activity.unregisterReceiver(mIsLoginReceiver);
        activity.unregisterReceiver(sLogoutReceiver);
        activity.unregisterReceiver(sPayResultReceiver);
        // 销毁悬浮窗
        mFloatViewService.destroyFloatView();
    }


    /**
     * 初始化成功的回调
     *
     *
     * @param initListener
     * @param isSuccess
     */
    private static void initCallBack(Activity activity, InitListener initListener, boolean isSuccess) {
        if(isSuccess){
            initListener.onInitSuccess(Const.INIT_SUCCESS);
            sInitSuccess = true;
        }else {
            initListener.onInitFail(Const.INIT_FAIL);
            ToastUtils.showErrorToast(activity, null, Const.INIT_FAIL);
            sInitSuccess = false;
        }

    }


    private static ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mFloatViewService = ((FloatViewService.FloatViewServiceBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mFloatViewService = null;
        }
    };


    /**
     * 检测登录是否成功的广播接收者
     */
    private static class IsLoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getStringExtra(Const.ISLOGIN).equals(Const.SUCCESS)) {
                sLoginSuccess = true;
                mFloatViewService.showFloatView();
            } else if (intent.getStringExtra(Const.ISLOGIN).equals(Const.FAIL)) {
                sLoginSuccess = false;
                mFloatViewService.hideFloatView();
            }

        }
    }


    /**
     * 检查注销是否成功的广播接收者
     */
    private static class LogoutReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (sLogoutListener != null) {
                sLogoutListener.onLogout(Const.LOGOUT_SUCCESS);
                // 改变登录状态
                sLoginSuccess = false;
            }
        }
    }


    /**
     * 检查支付是否成功的广播接收者
     */
    private static class PayResultReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            // 获取支付结果
            String result = intent.getStringExtra(Const.PAY_RESULT);

            if(result.equals(Const.PAY_SUCCESS)){
                // 回调支付成功
                sPayListener.onPaySuccess(Const.SUCCESS);

            }else if(result.equals(Const.PAY_CANCLE)){
                // 回调取消订单
                sPayListener.onPayCancle(Const.CANCLE);

            }else if(result.equals(Const.PAY_FAIL)){
                // 回调支付失败
                sPayListener.onPayFail(Const.FAIL);
            }


        }
    }




}
