package action.hdsdk.com.sdk.listener;

import org.json.JSONObject;

/**
 * Created by shake on 2017/6/20.
 * SDK的回调接口
 */
public interface InitListener {

    /**
     * 初始化成功的回调接口
     *
     * @param json
     */
    public void onInitSuccess(JSONObject json);


    /**
     * 初始化失败
     *
     * @param json
     */
    public void onInitFail(JSONObject json);

}
