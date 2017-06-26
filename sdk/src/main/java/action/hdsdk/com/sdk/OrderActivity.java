package action.hdsdk.com.sdk;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import action.hdsdk.com.sdk.http.OkHttpHelper;
import action.hdsdk.com.sdk.listener.PayListener;

public class OrderActivity extends BaseActivity {

    private Activity mActivity;
    private PayListener mPayListener;
    private OkHttpHelper mOkHttpHelper;

    private TextView mTvPayName;
    private TextView mTvPayAmount;
    private TextView mTvPayInfo;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // 初始化Views
        initViews();

        // 初始化数据
        initDatas();

    }


    /**
     * 初始化数据
     */
    private void initDatas() {
        Intent intent = getIntent();



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
    }
}
