package action.hdsdk.com.sdk.dialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import action.hdsdk.com.sdk.Const;
import action.hdsdk.com.sdk.utils.Utils;

/**
 * Created by xk on 2017/6/20.
 */
public class BaseDialog extends Dialog {

    private Context mContext;
    private String mEvent;

    public BaseDialog(Context context,String event) {
        super(context);
        init(context, event);
    }


    public BaseDialog(Context context, int themeResId,String event) {
        super(context, themeResId);
        init(context, event);
    }

    private void init(Context context,String event) {
        this.mContext = context;
        this.mEvent = event;
        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        // 背景色透明
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }

        // 注册广播接接收者,监听home键
        mContext.registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        // 设置不可以点击外部的东西
        setCanceledOnTouchOutside(false);

        int[] windowSize = onLayoutCallBack();


        // 设置取消事件
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                closdDialog();
            }
        });


        // 设置返回按钮取消的
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    closdDialog();
                    // 这里应该return true的
                    return true;
                }
                return false;
            }
        });


    }

    /**
     * @param view
     */
    @Override
    public void setContentView(View view) {
        int[] point = onLayoutCallBack();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(point[0], point[1]);
        Utils.log(BaseDialog.class, "onLayoutCallBack,window的宽：" + point[0] + ",window的高：" + point[1]);
        super.setContentView(view, params);
    }


    protected int[] onLayoutCallBack() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        // TODO 1、这里可以去了解一些有关dx和dp等尺寸的转换
        // 转变为标准尺寸。40是数值。看资料，好像是将别的单位，转换为px。http://blog.csdn.net/voo00oov/article/details/45745819
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40F, mContext.getResources().getDisplayMetrics());
        Utils.log(BaseDialog.class, "onLayoutCallBack,sapce是什么：" + space);
        // 获取屏幕尺寸
        Point point = Utils.getScreenSize(mContext);
        int width = Math.min(point.x, point.y);
        int height = Math.max(point.x, point.y);

        // 设置Window的尺寸
        lp.width = width - space;
        //lp.height = height / 2;
        //lp.height = height / 2;

        // 自动登录框高度小一点
        if(mEvent == Const.AUTO_LOGIN_DIALOG){
            lp.height = height / 3;
        }else {
            lp.height = height * 2 / 5;
        }
        dialogWindow.setAttributes(lp);
        Utils.log(BaseDialog.class, "onLayoutCallBack,屏幕的宽：" + width + ",屏幕的高：" + height);
        return new int[]{lp.width, lp.height};
    }


    /**
     * 关闭对话框，并且解除广播接收者
     */
    private void closdDialog() {
        if (isShowing()) {
            dismiss();
            mContext.unregisterReceiver(mHomeKeyEventReceiver);
        }
    }

    /**
     * 监听是否点击了home键将客户端推到后台
     */
    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    //表示按了home键,程序到了后台
                    Utils.log(BaseDialog.class, "监听到了home键，关闭对话框");
                    closdDialog();
                } else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
                    //表示长按home键,显示最近使用的程序列表
                }
            }
        }
    };

}
