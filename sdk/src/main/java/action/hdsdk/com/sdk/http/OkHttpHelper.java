package action.hdsdk.com.sdk.http;


import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import action.hdsdk.com.sdk.utils.Utils;

/**
 * Created by shake on 2017/6/20.
 * 网络工具类
 */
public class OkHttpHelper {


    private static OkHttpHelper mInstance;  // 封装类实例
    private static OkHttpClient sOkHttpClient; // OkHttpClient实例
    private static Handler sHandler;

    static {
        mInstance = new OkHttpHelper();
    }

    /**
     * 获取封装类实例
     *
     * @return
     */
    public static OkHttpHelper getInstance() {
        return mInstance;
    }


    private OkHttpHelper() {
        sOkHttpClient = new OkHttpClient();
        sOkHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        sOkHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        sOkHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);

        sHandler = new Handler(Looper.myLooper());
    }


    /**
     * 对外提供的get方法
     *
     * @param url
     * @param callback
     */
    public void get(String url, BaseHttpCallback callback) {
        // 请求网络之前，调用回调接口
        callback.onRequestBefore();
        // 1,构建Request对象
        Request request = buildRequest(url, null, HttpMethodType.GET);
        // 2,去请求数据
        doRequest(request, callback);
    }

    /**
     * 对外提供的post方法
     *
     * @param url
     * @param params
     * @param callback
     */
    public void post(String url, Map<String, Object> params, BaseHttpCallback callback) {
        // 请求网络之前，调用回调接口
        callback.onRequestBefore();
        // 1,构建Request对象
        Request request = buildRequest(url, params, HttpMethodType.POST);
        // 2,去请求数据
        doRequest(request, callback);
    }


    /**
     * 请求网络
     *
     * @param request
     * @param callback
     */
    private void doRequest(final Request request, final BaseHttpCallback callback) {

//        // 请求网络之前，调用回调接口
//        callback.onRequestBefore();


        // 去请求网络
        sOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                // 请求失败的回调
                callbackFailure(callback, request, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                // 请求能有正常响应
                callbackResponse(callback, response);
                // 获取请求结果
                String resultStr = response.body().string();
                Utils.log(OkHttpHelper.class,"请求到的数据:"+resultStr);
                try {
                    JSONObject jsonResult = new JSONObject(resultStr);
                    if (response.isSuccessful()) {
                        // 返回请求成功的数据
                        callbackSuccess(callback, jsonResult);

                    } else {
                        callbackError(callback, jsonResult);
                    }

                } catch (JSONException e) {


                    // 发生json异常
                    callbackError(callback, null);
                }


            }
        });
    }


    /**
     * 响应错误的回调。因为有可能是从网络线程回调到UI线程
     *
     * @param callback
     */
    private void callbackError(final BaseHttpCallback callback, final JSONObject json) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(json);
            }
        });
    }


    /**
     * 请求成功的回调。因为有可能是从网络线程回调到UI线程
     *
     * @param callback
     * @param json
     */
    private void callbackSuccess(final BaseHttpCallback callback, final JSONObject json) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(json);
            }
        });

    }


    /**
     * 请求有相响应的回调。因为有可能是从网络线程回调到UI线程
     *
     * @param callback
     * @param response
     */
    private void callbackResponse(final BaseHttpCallback callback, final Response response) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(response);
            }
        });
    }


    /**
     *
     *
     * @param request
     * @param e
     */
    private void callbackFailure(final BaseHttpCallback callback, final Request request, final IOException e) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                // 请求失败的回调
                callback.onFailure(request, e);
            }
        });
    }


    /**
     * 构建Request
     *
     * @param url
     * @param params
     * @return
     */
    private Request buildRequest(String url, Map<String, Object> params, HttpMethodType methodType) {
        // 封装Request对象
        Request.Builder builder = new Request.Builder();
        builder.url(url);

        if (methodType == HttpMethodType.GET) {
            builder.get();
        } else if (methodType == HttpMethodType.POST) {
            RequestBody body = buildBodyFromParams(params);
            builder.post(body);
        }
        return builder.build();
    }

    /**
     * 封装post的参数
     *
     * @param params
     * @return
     */
    private RequestBody buildBodyFromParams(Map<String, Object> params) {
        FormEncodingBuilder encodingBuilder = new FormEncodingBuilder();
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                encodingBuilder.add(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
            }
        }
        return encodingBuilder.build();
    }


    /**
     * http方法
     */
    enum HttpMethodType {
        GET,
        POST
    }

}
