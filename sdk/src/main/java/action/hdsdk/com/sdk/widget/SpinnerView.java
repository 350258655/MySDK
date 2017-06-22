package action.hdsdk.com.sdk.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import action.hdsdk.com.sdk.R;


/**
 * Created by shake on 17-6-3.
 * 下拉选择框
 */
public class SpinnerView extends RelativeLayout {

    private EditText mEtInput;
    private ImageView mIvArrow;

    private ListAdapter mAdapter;
    private PopupWindow mWindow;

    private ListView mListView;
    //监听器
    private AdapterView.OnItemClickListener mListener;

    public SpinnerView(Context context) {
        this(context, null);
    }

    public SpinnerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpinnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化View
        init(context);


    }

    private void init(Context context) {
        View.inflate(context, R.layout.spinner_view, this);
        mEtInput = (EditText) findViewById(R.id.et_input);
        mIvArrow = (ImageView) findViewById(R.id.iv_arrow);

        //点击弹出数据
        mIvArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAdapter != null){
                    clickArrow();
                }
            }
        });

    }

    /**
     * 点击弹出数据
     */
    private void clickArrow() {

        /** 设置数据，并且弹出PopWindow */
        if (mWindow == null) {

            /** 1、构建数据，即构建 PopWindow需要的参数 */
            mListView = new ListView(getContext());
            mListView.setAdapter(mAdapter);
            mListView.setBackgroundResource(R.drawable.listview_background);
            //获取编辑框的宽度
            int width = mEtInput.getWidth();
            //设置一个高度
            int height = 600;

            /** 2、构建 PopWindow需要的参数 */
            mWindow = new PopupWindow(mListView, width, height);
            //设置获取焦点，否则焦点会被EditText夺取吧
            mWindow.setFocusable(true);
            //设置点击边缘收起
            mWindow.setOutsideTouchable(true);
            mWindow.setBackgroundDrawable(new ColorDrawable());

        }

        /** 设置监听器 */
        if (mListener != null) {
            mListView.setOnItemClickListener(mListener);
        }

        /** 设置Window显示在EditText之下 */
        mWindow.showAsDropDown(mEtInput);
    }


    /**
     * 提供给外面调用的设置数据的方法
     *
     * @param adapter
     */
    public void setAdapter(ListAdapter adapter) {
        this.mAdapter = adapter;
    }


    /**
     * 提供给外面的设置监听器的方法。这个监听器不是我们自己定义的，我们只是用系统提供的监听器做个转接而已
     *
     * @param listener
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mListener = listener;
    }

    /**
     * 提供给外面的设置监听器的方法。设置编辑框的数据
     *
     * @param data
     */
    public void setText(String data) {
        mEtInput.setText(data);
    }

    /**
     * 提供给外面的设置监听器的方法。设置编辑框光标的位置
     * @param length
     */
    public void setSelection(int length){
        mEtInput.setSelection(length);
    }


    /**
     * 提供给外面的设置监听器的方法。关闭PopupWindow
     */
    public void dismiss(){
        if(mWindow != null){
            mWindow.dismiss();
        }
    }

}
