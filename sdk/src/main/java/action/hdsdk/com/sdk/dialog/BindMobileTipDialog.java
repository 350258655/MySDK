package action.hdsdk.com.sdk.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import action.hdsdk.com.sdk.BindPhoneActivity;
import action.hdsdk.com.sdk.Const;
import action.hdsdk.com.sdk.R;
import action.hdsdk.com.sdk.http.OkHttpHelper;

/**
 * Created by shake on 2017/6/22.
 */
public class BindMobileTipDialog extends BaseDialog {

    Button btn_bind;
    Button btn_unbind;
    OkHttpHelper mOkHttpHelper;
    Context mContext;


    public BindMobileTipDialog(final Context context, final String userName) {
        super(context,Const.BIND_PHONE_DIALOG);
        View view = LayoutInflater.from(context).inflate(R.layout.hd_dialog_bind_mobile_tip,null);
        setContentView(view);

        // 找到View
        btn_bind = findViewById(R.id.btn_bind);
        btn_unbind = findViewById(R.id.btn_unbind);

        mOkHttpHelper = OkHttpHelper.getInstance();
        mContext = context;

        // 不绑定则消失
        btn_unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // 绑定则跳转到绑定界面
        btn_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, BindPhoneActivity.class);
                intent.putExtra(Const.CURRENT_USER,userName);
                context.startActivity(intent);
                dismiss();
            }
        });

    }
}
