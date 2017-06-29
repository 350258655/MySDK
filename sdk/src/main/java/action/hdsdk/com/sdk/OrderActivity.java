package action.hdsdk.com.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import action.hdsdk.com.sdk.alipay.PartnerConfig;
import action.hdsdk.com.sdk.alipay.PayResult;
import action.hdsdk.com.sdk.alipay.SignUtils;
import action.hdsdk.com.sdk.db.PreferencesUtils;
import action.hdsdk.com.sdk.dialog.ExitDialog;
import action.hdsdk.com.sdk.http.API;
import action.hdsdk.com.sdk.http.HttpCallback;
import action.hdsdk.com.sdk.http.OkHttpHelper;
import action.hdsdk.com.sdk.listener.PayListener;
import action.hdsdk.com.sdk.utils.ToastUtils;
import action.hdsdk.com.sdk.utils.Utils;

public class OrderActivity extends BaseActivity implements View.OnClickListener {

    private OkHttpHelper mOkHttpHelper;

    private TextView mTvPayName;
    private TextView mTvPayAmount;
    private TextView mTvPayInfo;
    private TextView mTvPayTotalAmount;
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
    private String mProductName; // 产品名字
    private String mProductInfo; // 产品描述
    private String mOrderNum; // 订单号
    private String mAccess_token;
    private Toolbar mToolbar;
    private Button mBtn_createOrder; // 提交订单的按钮
    private String mPayChannel = Const.CHANNEL_ALIPAY; // 当前支付的渠道，默认是支付宝
    private ExitDialog mExitDialog;

    /**
     * 存放渠道和对应的RadioButton
     */
    private HashMap<String, RadioButton> channels = new HashMap<>(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

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

        // 初始化一些变量
        mOkHttpHelper = OkHttpHelper.getInstance();
        mAccess_token = HDApplication.access_token;
        if (mAccess_token == null || mAccess_token.equals("")) {
            mAccess_token = PreferencesUtils.getString(this, Const.ACCESS_TOKEN);
        }

        // 支付方式的选择
        rl_alipay.setOnClickListener(this);
        rl_wechat.setOnClickListener(this);

        // 创建订单的按钮
        mBtn_createOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrder();
            }
        });


        // 取消订单
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 显示取消订单的对话框
                showCancleOrderDialog();
            }
        });

    }


    /**
     * 显示取消订单的对话框
     */
    private void showCancleOrderDialog() {

        mExitDialog = new ExitDialog.Builder(this)
                .setTitle(Const.CANCLE_ORDER_DIALOG_TITLE)
                .setContent(Const.CANCLE_ORDER_DIALOG_MESSAGE)
                .setPositiveButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 取消订单
                        cancleOrder();
                        mExitDialog.dismiss();
                    }
                })
                .setNegativeButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mExitDialog.dismiss();
                    }
                })
                .build();

        // 显示对话框
        mExitDialog.show();
    }


    /**
     * 取消订单
     */
    private void cancleOrder() {
        // 发送取消订单的广播
        Intent intent = new Intent();
        intent.setAction(Const.ACTION_PAY);
        intent.putExtra(Const.PAY_RESULT, Const.PAY_CANCLE);
        sendBroadcast(intent);

        // 结束当前的Activity
        finish();
        ToastUtils.showErrorToast(getApplicationContext(), null, "订单已取消！");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 显示取消订单的对话框
            showCancleOrderDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 创建订单
     */
    private void createOrder() {

        try {

            String url = API.GAME_CREATE_ORDER +
                    "&access_token=" + URLEncoder.encode(mAccess_token, "utf-8") +
                    "&amount=" + URLEncoder.encode(String.valueOf(mPayAmount), "utf-8") +
                    "&notifyurl=" + URLEncoder.encode(mNotifyUrl, "utf-8") +
                    "&exorderno=" + URLEncoder.encode(mExOrderNum, "utf-8") +
                    "&player=" + URLEncoder.encode(mRoleId, "utf-8") +
                    "&server=" + URLEncoder.encode(mServerId, "utf-8") +
                    "&remark=" + URLEncoder.encode(mProductName, "utf-8") +
                    "&desc=" + URLEncoder.encode(mProductInfo, "utf-8") +
                    "&extend_info=" + URLEncoder.encode(mExInfo, "utf-8") +
                    "&s=" + Const.s;

            Utils.log(OrderActivity.class, "创建订单的URL：" + url);

            mOkHttpHelper.get(url, new HttpCallback(this) {
                @Override
                public void onSuccess(JSONObject json) {
                    Utils.log(OrderActivity.class, "创建订单的结果：" + json);
                    dealWithSuccess(json, Const.CREATE_ORDER);
                }

                @Override
                public void onError(JSONObject json) {
                    ToastUtils.showErrorToast(getApplicationContext(), json, null);
                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void dealWithSuccess(JSONObject json, String event) {

        try {
            if (json.getString("code").equals("1")) {
                if (event.equals(Const.CREATE_ORDER)) {
                    dealWithSuccessCreateOrderSuccess(json);
                } else if (event.equals(Const.CHECK_ORDER)) {
                    dealWithSuccessCheckOrderSuccess(json);
                } else if (event.equals(Const.GET_WX_PARAMS)) {
                    dealWithGetWXParamsSuccess(json);
                }
            }else if(json.getString("code").equals("0")){

                // 微信支付有时候会出问题
                if(event.equals(Const.CHECK_ORDER)){
                    // 再去检查订单
                    orderCheck();
                }
            }



        } catch (JSONException e) {
            ToastUtils.showErrorToast(this, null, e.getMessage());
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
    private void dealWithSuccessCreateOrderSuccess(JSONObject json) {
        try {
            mOrderNum = json.getJSONObject("result").getString("order_no");
            if (mPayChannel.equals(Const.CHANNEL_ALIPAY)) {
                // 支付宝支付
                goAliPay(mOrderNum);
            } else if (mPayChannel.equals(Const.CHANNEL_WECHAT)) {
                goWechatPay(mOrderNum);
            }

        } catch (JSONException e) {
            ToastUtils.showErrorToast(getApplicationContext(), null, e.getMessage());
        }
    }


    /**
     * 初始化数据
     */
    private void initDatas() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(Const.ORDER_INFO);
        mProductName = bundle.getString(Const.ORDER_PRODUCT_NAME);
        mPayAmount = bundle.getDouble(Const.ORDER_AMOUNT);
        mNotifyUrl = bundle.getString(Const.ORDER_NOTIFYURL);
        mExOrderNum = bundle.getString(Const.ORDER_EX_ORDERNUM);
        mRoleId = bundle.getString(Const.ORDER_ROLEID);
        mServerId = bundle.getString(Const.ORDER_SERVERID);
        mExInfo = bundle.getString(Const.ORDER_EX_INFO);
        mProductInfo = bundle.getString(Const.ORDER_PROCUCT_INFO);

        // 显示到界面上
        mTvPayName.setText(mProductName);
        mTvPayInfo.setText(mProductInfo);
        mTvPayAmount.setText(String.valueOf(mPayAmount) + " 元");
        mTvPayTotalAmount.setText("￥" + mPayAmount + " 元");

        // 添加支付渠道
        channels.put(Const.CHANNEL_ALIPAY, mRbAlipay);
        channels.put(Const.CHANNEL_WECHAT, mRbWechat);
    }


    /**
     * 初始化Views
     */
    private void initViews() {
        mTvPayName = findViewById(R.id.tv_product_name);
        mTvPayAmount = findViewById(R.id.tv_amount);
        mTvPayInfo = findViewById(R.id.tv_des);
        rl_alipay = findViewById(R.id.rl_alipay);
        rl_wechat = findViewById(R.id.rl_wechat);
        mRbAlipay = findViewById(R.id.rb_alipay);
        mRbWechat = findViewById(R.id.rb_webchat);
        mToolbar = findViewById(R.id.order_toolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        mTvPayTotalAmount = findViewById(R.id.txt_total);
        mBtn_createOrder = findViewById(R.id.btn_create_order);
    }

    @Override
    public void onClick(View view) {
        //选择支付的渠道
        mPayChannel = view.getTag().toString();
        selecePayChannel();
    }

    /**
     * 选择支付的渠道
     */
    private void selecePayChannel() {
        for (Map.Entry<String, RadioButton> entry : channels.entrySet()) {

            RadioButton rb = entry.getValue();
            //被选中的那个就反选
            if (entry.getKey().equals(mPayChannel)) {
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

    }


    /**
     * 获取微信支付的参数
     *
     * @param json
     */
    private void dealWithGetWXParamsSuccess(JSONObject json) {
        Utils.log(OrderActivity.class, "请求微信支付的参数:" + json);
        try {
            String param = json.getJSONObject("result").getString("link");

            String params[] = param.split("&");
            //TODO 传递支付参数
            // 去执行微信支付
            Intent intent = new Intent();
            intent.putExtra("token_id", params[0]);
            intent.putExtra("server_id", params[1]);
            intent.putExtra("amount", params[2]);
            intent.setClass(this, WftWXPayActivity.class);

            startActivityForResult(intent, Const.WX_CALLBACK);


        } catch (JSONException e) {
            ToastUtils.showErrorToast(HDApplication.getContext(), null, e.getMessage());
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Const.WX_PAY_SUCCESS){
            // 检查订单
            orderCheck();
        }else if(resultCode == Const.WX_PAY_FAIL){
            payFailCallback(null,"支付失败");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 检查订单
     */
    private void orderCheck() {
        // 检查订单的URL
        String url = API.GAME_CHECK_ORDER + "&order_no=" + mOrderNum + "&s=" + Const.s + "&access_token=" + mAccess_token;
        Utils.log(OrderActivity.class, "检查订单的URL：" + url);
        mOkHttpHelper.get(url, new HttpCallback(this) {
            @Override
            public void onSuccess(JSONObject json) {
                dealWithSuccess(json, Const.CHECK_ORDER);
            }

            @Override
            public void onError(JSONObject json) {
                payFailCallback(json,"");
            }
        });
    }


    /**
     * 微信支付
     *
     * @param orderNum
     */
    private void goWechatPay(String orderNum) {
        String url = API.GAME_GET_WECHAT_PARAMS + "&order_no=" + orderNum + "&s=" + Const.s + "&access_token=" + mAccess_token;
        Utils.log(OrderActivity.class, "获取微信支付参数的url:" + url);
        mOkHttpHelper.get(url, new HttpCallback(this) {
            @Override
            public void onSuccess(JSONObject json) {
                dealWithSuccess(json, Const.GET_WX_PARAMS);
            }

            @Override
            public void onError(JSONObject json) {
                ToastUtils.showErrorToast(getApplicationContext(), json, null);
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
                PayTask alipay = new PayTask(OrderActivity.this);
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
                        orderCheck();

                    } else {
                        // 支付失败则显示对话框
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            //TODO 回调结果

                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            //TODO 回调结果,用户取消订单
                            payFailCallback(null,resultInfo);
                        } else {
                            payFailCallback(null,resultInfo);
                        }
                    }
                    break;
                }
                case 2: {
                    payFailCallback(null,"支付失败");
                    break;
                }

                default:
                    break;
            }

        }


    };

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
     * 支付失败的回调
     *
     * @param json
     */
    private void payFailCallback(JSONObject json,String msg) {

        // 发送广播
        Intent intent = new Intent();
        intent.setAction(Const.ACTION_PAY);
        intent.putExtra(Const.PAY_RESULT, Const.PAY_FAIL);
        // 二者选一
        if(json == null || json.equals("")){
            intent.putExtra(Const.PAY_FAIL_INFO,msg);
        }else if(msg == null || msg.equals("")){
            intent.putExtra(Const.PAY_FAIL_INFO, json.toString());
        }
        sendBroadcast(intent);

        ToastUtils.showErrorToast(getApplicationContext(), null, msg);
    }

    /**
     * 支付成功的回调
     *
     * @param json
     */
    private void paySuccessCallback(JSONObject json) {

        // 发送广播
        Intent intent = new Intent();
        intent.setAction(Const.ACTION_PAY);
        intent.putExtra(Const.PAY_RESULT, Const.PAY_SUCCESS);
        sendBroadcast(intent);

        // 结束当前Activity
        finish();

        ToastUtils.showErrorToast(HDApplication.getContext(), null, "支付成功！");
    }


}
