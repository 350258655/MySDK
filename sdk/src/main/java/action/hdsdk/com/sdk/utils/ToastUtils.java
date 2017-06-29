package action.hdsdk.com.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shake on 2017/6/21.
 */
public class ToastUtils {

    /**
     * 显示错误的吐司,假如是有自己自定义信息的，就显示自己的定义的
     */
    public static void showErrorToast(Context context, JSONObject json, String customMsg) {
        try {
            if (customMsg == null || customMsg.equals("")) {
                if(json != null){
                    String msg = json.getString("message");
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, customMsg, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
