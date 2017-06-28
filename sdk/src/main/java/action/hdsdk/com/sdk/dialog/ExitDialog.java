package action.hdsdk.com.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import action.hdsdk.com.sdk.R;


/**
 * Created by shake on 2017/6/28.
 */
public class ExitDialog extends Dialog {


    private TextView mTvTitle;
    private TextView mTvMsg;
    private Button mBtnConfirm;
    private Button mBtnCancle;
    private String mTitle;
    private String mMessage;
    private  View.OnClickListener onPositiveListener ; // 确定按钮的监听器
    private  View.OnClickListener onNegativeListener ; // 取消按钮的监听器
    public ExitDialog(Context context) {
        super(context, R.style.MyExitDialog);
        setContentView(R.layout.dialog_exit);
        initViews();
        initEvents();

    }


    private void initEvents() {

    }

    private void initViews() {
        mTvTitle = (TextView) findViewById(R.id.tv_exit_title);
        mTvMsg = (TextView) findViewById(R.id.tv_exit_content);
        mBtnCancle = (Button) findViewById(R.id.btn_dialog_cancle);
        mBtnConfirm = (Button) findViewById(R.id.btn_dialog_confirm);
    }

    @Override
    public void show() {
        if (!isShowing()) {

            // 设置数据
            if (mTitle == null || mTitle.equals("")) {
                Toast.makeText(getContext(), "必须设置对话框标题!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mMessage == null || mMessage.equals("")) {
                Toast.makeText(getContext(), "必须设置对话框内容!", Toast.LENGTH_SHORT).show();
                return;
            }
            mTvTitle.setText(mTitle);
            mTvMsg.setText(mMessage);

            // 设置监听事件
            if(onPositiveListener != null){
                mBtnConfirm.setOnClickListener(onPositiveListener);
            }

            if(onNegativeListener != null){
                mBtnCancle.setOnClickListener(onNegativeListener);
            }

            // 显示对话框
            super.show();
        }
    }


    //
//    @Override
//    public void show() {
//        if(!isShowing()){
//            super.show();
//        }
//    }
//
//
//    @Override
//    public void dismiss() {
//        if(isShowing()){
//            super.dismiss();
//        }
//    }

    public static class Builder {

        private ExitDialog mExitDialog;

        public Builder(Context context) {
            mExitDialog = new ExitDialog(context);
        }

        public Builder setTitle(String title) {
            mExitDialog.mTitle = title;
            return this;
        }

        public Builder setContent(String msg) {
            mExitDialog.mMessage = msg;
            return this;
        }

        public Builder setPositiveButton(View.OnClickListener listener){
           mExitDialog.onPositiveListener = listener;
            return this;
        }

        public Builder setNegativeButton(View.OnClickListener listener){
            mExitDialog.onNegativeListener = listener;
            return this;
        }

        public ExitDialog build() {
            return mExitDialog;
        }


    }
}
