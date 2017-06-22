package action.hdsdk.com.sdk.listener;

import org.json.JSONObject;

/**
 * Created by shake on 2017/6/21.
 */
public interface LoginListener {

    /**
     * 登录成功的回调接口
     *
     * @param json
     */
    public void onLoginSuccess(JSONObject json);


    /**
     * 登录失败的回调接口
     *
     * @param json
     */
    public void onLoginFail(JSONObject json);
}
