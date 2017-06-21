package action.hdsdk.com.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import action.hdsdk.com.sdk.R;

/**
 * Created by shake on 2017/6/20
 * 进度框.
 */
public class ProgressDialogUtils {
    private static Dialog loadingDialog;

    public static void show(Context context, String msg) {

        /** 第一步，加载布局 **/
        LayoutInflater inflater = LayoutInflater.from(context);
        // 加载View
        View v = inflater.inflate(R.layout.dialog_loading, null);
        // 加载布局
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_loading_view);
        // 获取提示文字
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        // 设置文字
        tipTextView.setText(msg);

        /** 第二步，创建自定义样式的dialog **/
        if (loadingDialog == null) {
            loadingDialog = new Dialog(context, R.style.MyDialogStyle);
            // 是否可以按“返回键”消失
            loadingDialog.setCancelable(true);
            // 点击加载框以外的区域
            loadingDialog.setCanceledOnTouchOutside(false);
            // 设置布局
            loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        /** 第三步，显示dialog **/
        loadingDialog.show();

    }

    /**
     * 关闭Dialog
     */
    public static void close() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
