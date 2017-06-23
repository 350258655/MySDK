package action.hdsdk.com.sdk.http;

import android.content.Context;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

import action.hdsdk.com.sdk.dialog.ProgressDialogUtils;

/**
 * Created by shake on 2017/6/20.
 * 网络请求回调接口
 */
public abstract class HttpCallback extends BaseHttpCallback {

    private Context mContext;
    private String msg;

    public HttpCallback(Context context) {
        mContext = context;
        this.msg = "加载中";
    }

    public HttpCallback(Context context,String msg) {
        mContext = context;
        this.msg = msg;
    }

    /**
     * 加载网络之前需要显示加载对话框
     */
    @Override
    public void onRequestBefore() {
        ProgressDialogUtils.show(mContext, msg);
    }

    /**
     * 加载网络失败需要关闭加载对话框
     *
     * @param request
     * @param e
     */
    @Override
    public void onFailure(Request request, IOException e) {
        ProgressDialogUtils.close();
    }


    /**
     * 在请求有响应之后也要关闭加载对话框
     *
     * @param response
     */
    @Override
    public void onResponse(Response response) {
        ProgressDialogUtils.close();
    }
}
