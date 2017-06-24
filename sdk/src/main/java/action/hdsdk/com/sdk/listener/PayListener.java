package action.hdsdk.com.sdk.listener;

import org.json.JSONObject;

/**
 * Created by shake on 2017/6/24 0024.
 * 支付的回调接口
 */
public interface PayListener {

    /**
     * 支付成功
     *
     * @param json
     */
    public void onPaySuccess(JSONObject json);


    /**
     * 支付失败
     *
     * @param json
     */
    public void onPayFail(JSONObject json);

}
