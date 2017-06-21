package action.hdsdk.com.sdk.http;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by shake on 2017/6/20.
 * 网络请求回调接口
 */
public abstract class BaseHttpCallback {

    /**
     * 在请求网络之前调用的方法
     */
    public abstract void onRequestBefore();


    /**
     * 在请求网络失败的时候调用的方法
     *
     * @param request
     * @param e
     */
    public abstract void onFailure(Request request, IOException e);

    /**
     * 请求成功时调用此方法，用于关闭loading对话框
     *
     * @param response
     */
    public abstract void onResponse(Response response);


    /**
     * 在请求网络成功的时候调用的方法。返回泛型，用户就不需要再从Response中去解析
     *
     * @param json
     */
    public abstract void onSuccess(JSONObject json);

    /**
     * 在请求网络返回发生错误的时候调用的方法
     *
     * @param json
     */
    public abstract void onError(JSONObject json);

}
