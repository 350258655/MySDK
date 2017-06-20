package action.hdsdk.com.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by xk on 2017/6/20.
 */
public class BaseDialog extends Dialog {

    private Context mContext;

    public BaseDialog(Context context) {
        super(context);
        init(context);
    }



    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        // 背景色透明
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }

        // 设置不可以点击外部的东西
        setCanceledOnTouchOutside(false);

        onLayoutCallBack();
    }


    private void onLayoutCallBack() {

    }
}
