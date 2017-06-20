package action.hdsdk.com.sdk;

import android.content.Context;

import action.hdsdk.com.sdk.dialog.BaseDialog;
import action.hdsdk.com.sdk.dialog.SplashDialog;

/**
 * Created by xk on 2017/6/19.
 */
public class HDSDK {

    private HDSDK(){

    }

    public static void initialize(Context context){

        // 显示 splash
        SplashDialog splashDialog = new SplashDialog(context);
        splashDialog.show();

        // 测试BaseDialog的数据
        BaseDialog baseDialog = new BaseDialog(context);


    }


}
