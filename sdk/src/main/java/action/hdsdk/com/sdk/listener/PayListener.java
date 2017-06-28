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
     * @param msg
     */
    public void onPaySuccess(String msg);


    /**
     * 取消支付
     *
     * @param msg
     */
    public void onPayCancle(String msg);


    /**
     * 支付失败
     *
     * @param msg
     */
    public void onPayFail(String msg);

}
