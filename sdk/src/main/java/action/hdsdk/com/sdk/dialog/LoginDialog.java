package action.hdsdk.com.sdk.dialog;

import android.content.Context;

import action.hdsdk.com.sdk.R;

/**
 * Created by shake on 2017/6/20.
 * 登录框
 */
public class LoginDialog extends BaseDialog {
    public LoginDialog(Context context) {
        super(context);
        setContentView(R.layout.hd_dialog_login);
    }


}
