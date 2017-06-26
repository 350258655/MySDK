package action.hdsdk.com.sdk.dialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import action.hdsdk.com.sdk.Const;
import action.hdsdk.com.sdk.HDApplication;
import action.hdsdk.com.sdk.R;
import action.hdsdk.com.sdk.WftWXPayActivity;
import action.hdsdk.com.sdk.alipay.PartnerConfig;
import action.hdsdk.com.sdk.alipay.PayResult;
import action.hdsdk.com.sdk.alipay.SignUtils;
import action.hdsdk.com.sdk.db.PreferencesUtils;
import action.hdsdk.com.sdk.http.API;
import action.hdsdk.com.sdk.http.HttpCallback;
import action.hdsdk.com.sdk.http.OkHttpHelper;
import action.hdsdk.com.sdk.listener.PayListener;
import action.hdsdk.com.sdk.listener.WxPayListener;
import action.hdsdk.com.sdk.utils.ToastUtils;
import action.hdsdk.com.sdk.utils.Utils;
import dalvik.system.DexClassLoader;

/**
 * Created by shake on 2017/6/24 0024.
 */
public class OrderDialog extends BaseDialog implements View.OnClickListener {
    private Activity mActivity;
    private PayListener mPayListener;
    private OkHttpHelper mOkHttpHelper;

    private TextView mTvPayInfo;
    private TextView mTvPayAmount;
    private RelativeLayout rl_alipay;
    private RelativeLayout rl_wechat;
    private RadioButton mRbWechat;
    private RadioButton mRbAlipay;
    private String mPayInfo;
    private double mPayAmount;
    private String mNotifyUrl;
    private String mExOrderNum; //外部订单
    private String mRoleId;
    private String mServerId;
    private String mExInfo;
    private String mProductInfo;
    private String mOrderNum; // 订单号
    private String mAccess_token;
    private static boolean isCancle = false;  // 定义一个变量，来标识是否取消支付
    private WXPayReceiver mWXPayReceiver;

    /**
     * 存放渠道和对应的RadioButton
     */
    private HashMap<String, RadioButton> channels = new HashMap<>(2);


    public OrderDialog(Activity context, PayListener payListener, String payInfo, double amount, String notifyUrl, String exOrderNum, String roleId, String serverId, String exInfo, String productInfo) {
        super(context, Const.ORDER_DIALOG);

        View view = LayoutInflater.from(context).inflate(R.layout.hd_dialog_pay, null);
        setContentView(view);

        mActivity = context;
        mPayListener = payListener;
        mOkHttpHelper = OkHttpHelper.getInstance();
        mPayInfo = payInfo;
        mPayAmount = amount;
        mNotifyUrl = notifyUrl;
        mExOrderNum = exOrderNum;
        mRoleId = roleId;
        mServerId = serverId;
        mExInfo = exInfo;
        mProductInfo = productInfo;
        mWXPayReceiver = new WXPayReceiver();
        // 注册广播接收者
        mActivity.registerReceiver(mWXPayReceiver, new IntentFilter(Const.ACTION_PAY));

        // 初始化Views
        initViews();

        // 初始化数据
        initDatas();

        // 初始化事件
        initEvents();


    }

    /**
     * 初始化事件
     */
    private void initEvents() {
        // 设置返回按钮取消的
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    // 取消支付
                    ToastUtils.showErrorToast(HDApplication.getContext(), null, "取消订单!");
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        // 支付方式的选择
        rl_alipay.setOnClickListener(this);
        rl_wechat.setOnClickListener(this);

    }


    /**
     * 显示退出框
     */
    private void showExitDialog() {
        new AlertDialog.Builder(mActivity).setTitle("确认取消订单吗？")
                .setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 取消订单
                        dismiss();
                    }
                })
                .setNegativeButton("取消", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        mTvPayInfo.setText(mPayInfo);
        mTvPayAmount.setText("金额：" + String.valueOf(mPayAmount) + "元");

        // 添加支付渠道
        channels.put(Const.CHANNEL_ALIPAY, mRbAlipay);
        channels.put(Const.CHANNEL_WECHAT, mRbWechat);
    }

    /**
     * 初始化Views
     */
    private void initViews() {
        mTvPayInfo = findViewById(R.id.tv_pay_info);
        mTvPayAmount = findViewById(R.id.tv_pay_amount);
        rl_alipay = findViewById(R.id.rl_alipay);
        rl_wechat = findViewById(R.id.rl_wechat);
        mRbAlipay = findViewById(R.id.rb_alipay);
        mRbWechat = findViewById(R.id.rb_webchat);
    }

    @Override
    public void onClick(View view) {
        //选择支付的渠道
        selecePayChannel(view.getTag().toString());
    }

    /**
     * 选择支付的渠道
     *
     * @param paychannel
     */
    private void selecePayChannel(String paychannel) {
        for (Map.Entry<String, RadioButton> entry : channels.entrySet()) {

            RadioButton rb = entry.getValue();
            //被选中的那个就反选
            if (entry.getKey().equals(paychannel)) {
                boolean isCheck = rb.isChecked();
                if (isCheck) {
                    rb.setChecked(isCheck);
                } else {
                    rb.setChecked(!isCheck);
                }
            } else {
                //没被选中的就反选
                rb.setChecked(false);
            }

        }

        // 去后台创建订单
        createOrder(paychannel);


    }


    /**
     * 创建订单
     *
     * @param paychannel
     */
    private void createOrder(final String paychannel) {

        // 获取access_token
        mAccess_token = HDApplication.access_token;
        try {

            if (mAccess_token == null || mAccess_token.equals("")) {
                mAccess_token = PreferencesUtils.getString(mActivity, Const.ACCESS_TOKEN);
            }

            String url = API.GAME_CREATE_ORDER +
                    "&access_token=" + URLEncoder.encode(mAccess_token, "utf-8") +
                    "&amount=" + URLEncoder.encode(String.valueOf(mPayAmount), "utf-8") +
                    "&notifyurl=" + URLEncoder.encode(mNotifyUrl, "utf-8") +
                    "&exorderno=" + URLEncoder.encode(mExOrderNum, "utf-8") +
                    "&player=" + URLEncoder.encode(mRoleId, "utf-8") +
                    "&server=" + URLEncoder.encode(mServerId, "utf-8") +
                    "&remark=" + URLEncoder.encode(mPayInfo, "utf-8") +
                    "&desc=" + URLEncoder.encode(mProductInfo, "utf-8") +
                    "&extend_info=" + URLEncoder.encode(mExInfo, "utf-8") +
                    "&s=" + Const.s;

            Utils.log(OrderDialog.class, "创建订单的URL：" + url);

            mOkHttpHelper.get(url, new HttpCallback(mActivity, "创建订单中") {
                @Override
                public void onSuccess(JSONObject json) {
                    Utils.log(OrderDialog.class, "创建订单的结果：" + json);
                    dealWithSuccess(json, Const.CREATE_ORDER, paychannel);
                }

                @Override
                public void onError(JSONObject json) {
                    ToastUtils.showErrorToast(mActivity, json, null);
                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void dealWithSuccess(JSONObject json, String event, String paychannel) {

        try {
            if (json.getString("code").equals("1")) {
                if (event.equals(Const.CREATE_ORDER)) {
                    dealWithSuccessCreateOrderSuccess(json, paychannel);
                } else if (event.equals(Const.CHECK_ORDER)) {
                    dealWithSuccessCheckOrderSuccess(json);
                } else if (event.equals(Const.GET_WX_PARAMS)) {
                    dealWithGetWXParamsSuccess(json);
                }

            }
        } catch (JSONException e) {
            ToastUtils.showErrorToast(mActivity, null, e.getMessage());
        }


    }

    /**
     * 获取微信支付的参数
     *
     * @param json
     */
    private void dealWithGetWXParamsSuccess(JSONObject json) {
        Utils.log(OrderDialog.class, "请求微信支付的参数:" + json);
        try {
            String param = json.getJSONObject("result").getString("link");

            String params[] = param.split("&");
            //TODO 传递支付参数
            // 去执行微信支付
            Intent intent = new Intent();
            intent.putExtra("token_id", params[0]);
            intent.putExtra("server_id", params[1]);
            intent.putExtra("amount", params[2]);
            intent.setClass(mActivity, WftWXPayActivity.class);

            mActivity.startActivity(intent);


        } catch (JSONException e) {
            ToastUtils.showErrorToast(HDApplication.getContext(), null, e.getMessage());
        }


    }

    /**
     * 检查订单成功
     *
     * @param json
     */
    private void dealWithSuccessCheckOrderSuccess(JSONObject json) {
        paySuccessCallback(json);
    }

    /**
     * 创建订单成功之后
     *
     * @param json
     */
    private void dealWithSuccessCreateOrderSuccess(JSONObject json, String paychannel) {
        try {
            mOrderNum = json.getJSONObject("result").getString("order_no");
            if (paychannel.equals(Const.CHANNEL_ALIPAY)) {
                // 支付宝支付
                goAliPay(mOrderNum);
            } else if (paychannel.equals(Const.CHANNEL_WECHAT)) {
                goWechatPay(mOrderNum);
            }

            // 对话框消失
            dismiss();
        } catch (JSONException e) {
            ToastUtils.showErrorToast(mActivity, null, e.getMessage());
        }
    }


    /**
     * 微信支付
     *
     * @param orderNum
     */
    private void goWechatPay(String orderNum) {
        String url = API.GAME_GET_WECHAT_PARAMS + "&order_no=" + orderNum + "&s=" + Const.s + "&access_token=" + mAccess_token;
        Utils.log(OrderDialog.class, "获取微信支付参数的url:" + url);
        mOkHttpHelper.get(url, new HttpCallback(mActivity) {
            @Override
            public void onSuccess(JSONObject json) {
                dealWithSuccess(json, Const.GET_WX_PARAMS, Const.CHANNEL_WECHAT);
            }

            @Override
            public void onError(JSONObject json) {
                ToastUtils.showErrorToast(mActivity, json, null);
            }
        });

    }

    /**
     * 支付宝支付
     *
     * @param orderNum
     */
    private void goAliPay(String orderNum) {
        //获取支付宝订单信息
        String orderInfo = getAliPayOrderInfo(orderNum);

        // 对订单做RSA 签名
        String sign = SignUtils.sign(orderInfo, PartnerConfig.RSA_PRIVATE);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";


        //异步支付
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mActivity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 获取支付宝的订单信息
     *
     * @param orderNum
     */
    private String getAliPayOrderInfo(String orderNum) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + "2088111008275155" + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + "ddlegame@ddle.cn" + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + orderNum + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + "研发测试" + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + "研发测试" + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + "0.01" + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://tiger.hodogame.com/alipayquicknotify.php" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";


        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";


        Log.i("TAG", "CreateOrderActivity，getAliPayOrderInfo，生成支付宝支付信息: " + orderInfo);

        return orderInfo;

    }


    /**
     * 异步支付过程
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        //支付宝成功支付成功返回
                        //TODO 回调结果
                        // 到浩动后台检查订单
                        orderCheck(Const.CHANNEL_ALIPAY);

                    } else {
                        // 支付失败则显示对话框
                        show();
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            //TODO 回调结果

                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            //TODO 回调结果,用户取消订单
                            try {


                                payFailCallback(new JSONObject(resultInfo));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                payFailCallback(new JSONObject(resultInfo));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    try {
                        //TODO 回调结果
                        // 支付失败则显示对话框
                        show();
                        payFailCallback(new JSONObject("支付失败"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }

                default:
                    break;
            }

        }


    };


    /**
     * 检查订单
     */
    private void orderCheck(final String payChannel) {
        // 检查订单的URL
        String url = API.GAME_CHECK_ORDER + "&order_no=" + mOrderNum + "&s=" + Const.s + "&access_token=" + mAccess_token;
        Utils.log(OrderDialog.class, "检查订单的URL：" + url);
        mOkHttpHelper.get(url, new HttpCallback(mActivity) {
            @Override
            public void onSuccess(JSONObject json) {
                dealWithSuccess(json, Const.CHECK_ORDER, payChannel);
            }

            @Override
            public void onError(JSONObject json) {

            }
        });
    }

    /**
     * 支付成功的回调
     *
     * @param json
     */
    private void paySuccessCallback(JSONObject json) {
        mPayListener.onPaySuccess(json);
        // Utils.log(OrderDialog.class, "检查订单成功：" + json);
        // 关闭当前对话框
        dismiss();
        mActivity.unregisterReceiver(mWXPayReceiver);
        ToastUtils.showErrorToast(HDApplication.getContext(), null, "支付成功！");
    }

    /**
     * 支付失败的回调
     *
     * @param json
     */
    private void payFailCallback(JSONObject json) {
        mPayListener.onPayFail(json);
        mActivity.unregisterReceiver(mWXPayReceiver);
    }


    private class WXPayReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getStringExtra(Const.PAY_SUCCESS).equals(Const.SUCCESS)) {
                //Utils.log(OrderDialog.class, "微信支付成功！ ");
                // 订单校验
                orderCheck(Const.CHANNEL_WECHAT);

            } else if (intent.getStringExtra(Const.PAY_SUCCESS).equals(Const.FAIL)) {
                ToastUtils.showErrorToast(mActivity, null, "支付失败！");
            }
        }
    }


}
