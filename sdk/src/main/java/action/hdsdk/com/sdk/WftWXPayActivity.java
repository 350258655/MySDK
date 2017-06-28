package action.hdsdk.com.sdk;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;



public class WftWXPayActivity extends Activity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        //调起支付
        goToPay();
    }

    /**
     * 调起支付
     */
    private void goToPay() {
        RequestMsg msg = new RequestMsg();
        msg.setMoney(Double.parseDouble(intent.getStringExtra("amount")));
        msg.setTokenId(intent.getStringExtra("token_id"));
        // 微信wap支付
        msg.setTradeType(MainApplication.PAY_WX_WAP);
        PayPlugin.unifiedH5Pay(WftWXPayActivity.this, msg);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // 发送广播是针对dialog的做法
        //Intent intent = new Intent();
        //intent.setAction(Const.ACTION_PAY);
        if (data == null)
        {
            return;
        }

        String respCode = data.getExtras().getString("resultCode");
        if (!TextUtils.isEmpty(respCode) && respCode.equalsIgnoreCase("success"))
        {
            //标示支付成功
            //intent.putExtra(Const.PAY_SUCCESS,Const.SUCCESS);
            setResult(Const.WX_PAY_SUCCESS);
            finish();
        }
        else
        { //其他状态NOPAY状态：取消支付，未支付等状态
            //intent.putExtra(Const.PAY_FAIL, Const.FAIL);
            setResult(Const.WX_PAY_FAIL);
            finish();
        }

        // 发送广播
        //sendBroadcast(intent);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    @Override
    protected void onStop() {
        super.onStop();
    }



}
