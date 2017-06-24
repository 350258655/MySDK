package action.hdsdk.com.sdk.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import action.hdsdk.com.sdk.Const;
import action.hdsdk.com.sdk.HDApplication;
import action.hdsdk.com.sdk.R;
import action.hdsdk.com.sdk.http.OkHttpHelper;
import action.hdsdk.com.sdk.listener.PayListener;
import action.hdsdk.com.sdk.utils.ToastUtils;

/**
 * Created by shake on 2017/6/24 0024.
 */
public class OrderDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
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

    /**
     * 存放渠道和对应的RadioButton
     */
    private HashMap<String, RadioButton> channels = new HashMap<>(2);


    public OrderDialog(Context context, PayListener payListener,String payInfo,double amount) {
        super(context, Const.ORDER_DIALOG);

        View view = LayoutInflater.from(context).inflate(R.layout.hd_dialog_pay,null);
        setContentView(view);

        mContext = context;
        mPayListener = payListener;
        mOkHttpHelper = OkHttpHelper.getInstance();
        mPayInfo = payInfo;
        mPayAmount = amount;



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
                    // 显示对话框
                    ToastUtils.showErrorToast(HDApplication.getContext(),null,"订单已取消!");
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
        new AlertDialog.Builder(mContext).setTitle("确认取消订单吗？")
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
                if(isCheck){
                    rb.setChecked(isCheck);
                }else {
                    rb.setChecked(!isCheck);
                }
            } else {
                //没被选中的就反选
                rb.setChecked(false);
            }

        }



    }
}
