package action.hdsdk.com.sdk.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Timer;
import java.util.TimerTask;

import action.hdsdk.com.sdk.CSCenterActivity;
import action.hdsdk.com.sdk.R;
import action.hdsdk.com.sdk.UserCenterActivity;

/**
 * Created by shake on 2017/6/12 0012.
 */
public class FloatView extends FrameLayout implements View.OnTouchListener {

    private final int HANDLER_TYPE_HIDE_LOGO = 100;//隐藏LOGO
    private final int HANDLER_TYPE_CANCEL_ANIM = 101;//退出动画

    private WindowManager mWindowManager;
    private Context mContext;

    private int mScreenWidth; //屏幕宽
    private int mScreenHeight; //屏幕高
    private WindowManager.LayoutParams mWmParams; //窗口参数

    //private View mRootFloatView;
    private ImageView mIvFloatLogo;
    private ImageView mIvFloatLoader;
    private LinearLayout mLlFloatMenu;
    private TextView mTvAccount;
    private TextView mTvFeedback;
    private FrameLayout mFlFloatLogo;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean mDraging; //是否正在移动

    private boolean mIsRight;//logo是否在右边
    private boolean mCanHide;//是否允许隐藏
    private float mTouchStartX;
    private float mTouchStartY;
    private boolean mShowLoader = true; //显示旋转动画

    Handler mTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_TYPE_HIDE_LOGO) {
                // 隐藏悬浮框
                if (mCanHide) {
                    mCanHide = false;

                    LayoutParams params = (LayoutParams) mFlFloatLogo.getLayoutParams();
                    // 隐藏掉 2/3
                    int margin = params.width * 2 / 3;
                    if (mIsRight) {
                        // mIvFloatLogo.setImageResource(R.drawable.pj_image_float_right);
                        params.setMargins(0, 0, -margin, 0);
                        // 设置padding可以让整个小球看起来变小,因为有了padding，子view所占空间就会被压缩
                        // mFlFloatLogo.setPadding(0, 16, 16, 16);
                        mFlFloatLogo.setLayoutParams(params);
                    } else {

                        params.setMargins(-margin, 0, 0, 0);
                        // 设置padding可以让整个小球看起来变小,因为有了padding，子view所占空间就会被压缩
                        // mFlFloatLogo.setPadding(0, 16, 16, 16);
                        mFlFloatLogo.setLayoutParams(params);


                    }
                    //降低透明度
                    mWmParams.alpha = 0.7f;
                    mWindowManager.updateViewLayout(FloatView.this, mWmParams);
                    refreshFloatMenu(mIsRight);
                    mLlFloatMenu.setVisibility(GONE);
                }
            } else if (msg.what == HANDLER_TYPE_CANCEL_ANIM) {
                // 恢复悬浮窗原本状态
                resetLogoSize();
                // 隐藏“旋转”动画
                mIvFloatLoader.clearAnimation();
                mIvFloatLoader.setVisibility(GONE);
                mShowLoader = false;
            }
            super.handleMessage(msg);
        }
    };


    public FloatView(Context context) {
        super(context);
        this.mContext = context;
        init(mContext);
    }

    private void init(Context context) {
        /**
         * 第一步，获取窗口管理器，并且设置一系列的参数
         */
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        // 获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        Log.i("TAG", FloatView.class + ",init,获取屏幕宽高,宽：" + mScreenWidth + ",高：" + mScreenHeight);
        mWmParams = new WindowManager.LayoutParams();
        // 设置 window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        // 设置图片格式
        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗显示的停靠位置为左侧置顶
        mWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        mWmParams.x = 0;
        mWmParams.y = mScreenHeight / 2;

        // 设置悬浮窗口长宽数据
        mWmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        // 在当前的FrameLayout 加一个View
        addView(createView(mContext));


        /**
         * 第二步，窗口管理器添加View和参数。然后延时隐藏本View
         */
        mWindowManager.addView(this, mWmParams);
        mTimer = new Timer();
        hide();
    }


    /**
     * 创建Float View
     *
     * @param context
     * @return
     */
    private View createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        /**
         * 第一步 从布局文件获取浮动窗口视图
         */
        View rootFloatView = inflater.inflate(R.layout.widget_float_view, null);
        mFlFloatLogo = (FrameLayout) rootFloatView.findViewById(R.id.float_view);
        mIvFloatLogo = (ImageView) rootFloatView.findViewById(R.id.float_view_icon_imageView);
        mIvFloatLoader = (ImageView) rootFloatView.findViewById(R.id.float_view_icon_notify);
        mLlFloatMenu = (LinearLayout) rootFloatView.findViewById(R.id.ll_menu);
        mTvAccount = (TextView) rootFloatView.findViewById(R.id.tv_account);
        mTvFeedback = (TextView) rootFloatView.findViewById(R.id.tv_feedback);


        /**
         * 第二步 设置点击事件
         */
        // 设置子菜单的点击事件
        mTvAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏菜单
                mLlFloatMenu.setVisibility(GONE);
                // 打开个人中心
                Intent intent = new Intent(mContext, UserCenterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                mContext.startActivity(intent);
            }
        });
        mTvFeedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏菜单
                mLlFloatMenu.setVisibility(GONE);
                // 打开客服界面
                Intent intent = new Intent(mContext, CSCenterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                mContext.startActivity(intent);
            }
        });
        // 设置整个View的触摸事件
        rootFloatView.setOnTouchListener(this);
        // 设置整个View的点击事件
        rootFloatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 假如不是在移动状态
                if (!mDraging) {
                    if (mLlFloatMenu.getVisibility() == VISIBLE) {
                        mLlFloatMenu.setVisibility(GONE);
                    } else {
                        mLlFloatMenu.setVisibility(VISIBLE);
                    }
                }
            }
        });


        /**
         * 第三步，测量View,rootFloatView是一个FrameLayout
         */
        rootFloatView.measure(View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        return rootFloatView;
    }

    /**
     * 悬浮球缩进屏幕后，恢复原始状态
     */
    private void resetLogoSize() {

        int length = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mContext.getResources().getDisplayMetrics());
        LayoutParams params = (LayoutParams) mFlFloatLogo.getLayoutParams();
        params.width = length;
        params.height = length;
        params.setMargins(0, 0, 0, 0);
        mFlFloatLogo.setLayoutParams(params);
    }


    /**
     * getX()和getRawX()的区别
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 取消时间隐藏任务
        removeTimerTask();
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                // 恢复悬浮窗原本状态
                resetLogoSize();

                // 设置透明度
                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(this, mWmParams);
                mDraging = false;
                break;

            case MotionEvent.ACTION_MOVE:
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();

                // 如果移动量大于3才移动
                if (Math.abs(mTouchStartX - mMoveStartX) > 3 && Math.abs(mTouchStartY - mMoveStartY) > 3) {
                    mDraging = true;
                    // 更新浮动窗口位置参数
                    mWmParams.x = (int) (x - mTouchStartX);
                    mWmParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(this, mWmParams);
                    // 隐藏子菜单
                    mLlFloatMenu.setVisibility(GONE);
                    return false;
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mWmParams.x >= mScreenWidth / 2) {
                    // View停靠在右边
                    mWmParams.x = mScreenWidth;
                    mIsRight = true;
                } else if (mWmParams.x < mScreenWidth / 2) {
                    // View停靠在左边
                    mWmParams.x = 0;
                    mIsRight = false;
                }
                // 刷新界面
                refreshFloatMenu(mIsRight);
                // 启动隐藏定时任务
                timerForHide();
                // 更新window参数
                mWindowManager.updateViewLayout(this, mWmParams);
                // 把start的x和y置为0
                mTouchStartY = mTouchStartY = 0;
                break;
        }
        return false;
    }


    public void show() {
        // 恢复悬浮窗原本状态
        resetLogoSize();

        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
            if (mShowLoader) {
                // 更新界面参数
                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(this, mWmParams);

                // 定时隐藏
                timerForHide();

                mShowLoader = false;

                // 开启动画，随狗隐藏动画
                Animation rotaAnimation = AnimationUtils.loadAnimation(mContext, R.anim.hd_loading_anim);
                rotaAnimation.setInterpolator(new LinearInterpolator());
                mIvFloatLoader.startAnimation(rotaAnimation);
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mTimeHandler.sendEmptyMessage(HANDLER_TYPE_CANCEL_ANIM);
                    }
                }, 3000);

            }
        }
    }


    /**
     * 隐藏悬浮框。这个View在init()的时候是需要隐藏的，由show()接口控制悬浮框的显示，然后这个hide接口
     * 也暴露给外面，可以控制悬浮框的隐藏
     */
    public void hide() {
        setVisibility(GONE);
        Message message = mTimeHandler.obtainMessage();
        message.what = HANDLER_TYPE_HIDE_LOGO;
        mTimeHandler.sendMessage(message);
        removeTimerTask();
    }

    /**
     * 定时隐藏float view
     */
    private void timerForHide() {
        mCanHide = true;

        // 结束之前的任务
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        // 开始新任务
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = mTimeHandler.obtainMessage();
                message.what = HANDLER_TYPE_HIDE_LOGO;
                mTimeHandler.sendMessage(message);
            }
        };

        // 延迟发送消息
        if (mCanHide) {
            mTimer.schedule(mTimerTask, 6000, 3000);
        }
    }

    /**
     * 刷新界面
     *
     * @param isRight
     */
    private void refreshFloatMenu(boolean isRight) {
        if (isRight) {
            // 设置图片在菜单的右边
            FrameLayout.LayoutParams paramsFloatImage = (LayoutParams) mIvFloatLogo.getLayoutParams();
            paramsFloatImage.gravity = Gravity.RIGHT;
            mIvFloatLogo.setLayoutParams(paramsFloatImage);

            // 设置布局在右边
            FrameLayout.LayoutParams paramsFlFloat = (LayoutParams) mFlFloatLogo.getLayoutParams();
            paramsFlFloat.gravity = Gravity.RIGHT;
            mFlFloatLogo.setLayoutParams(paramsFlFloat);

            // 设置"个人中心"的界面
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams paramsMenuAccount = (LinearLayout.LayoutParams) mTvAccount.getLayoutParams();
            paramsMenuAccount.rightMargin = padding;
            paramsMenuAccount.leftMargin = padding;
            mTvAccount.setLayoutParams(paramsMenuAccount);

            // 设置"客服"的界面
            int padding52 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 52, mContext.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams paramsMenuFb = (LinearLayout.LayoutParams) mTvFeedback.getLayoutParams();
            paramsMenuFb.rightMargin = padding52;
            paramsMenuFb.leftMargin = padding;
            mTvFeedback.setLayoutParams(paramsMenuFb);
        } else {
            // 设置图片在菜单的左边
            FrameLayout.LayoutParams paramsFloatImage = (LayoutParams) mIvFloatLogo.getLayoutParams();
            paramsFloatImage.setMargins(0, 0, 0, 0);
            paramsFloatImage.gravity = Gravity.LEFT;
            mIvFloatLogo.setLayoutParams(paramsFloatImage);

            // 设置布局在左边
            FrameLayout.LayoutParams paramsFlFloat = (LayoutParams) mFlFloatLogo.getLayoutParams();
            paramsFlFloat.gravity = Gravity.LEFT;
            mFlFloatLogo.setLayoutParams(paramsFlFloat);

            // 设置"个人中心"的界面
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources().getDisplayMetrics());
            int padding52 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 52, mContext.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams paramsMenuAccount = (LinearLayout.LayoutParams) mTvAccount.getLayoutParams();
            paramsMenuAccount.leftMargin = padding52;
            paramsMenuAccount.rightMargin = padding;
            mTvAccount.setLayoutParams(paramsMenuAccount);

            // 设置"客服"的界面
            LinearLayout.LayoutParams paramsMenuFb = (LinearLayout.LayoutParams) mTvFeedback.getLayoutParams();
            paramsMenuFb.rightMargin = padding;
            paramsMenuFb.leftMargin = padding;
            mTvFeedback.setLayoutParams(paramsMenuFb);
        }
    }


    /**
     * 取消定时隐藏 float view任务
     */
    private void removeTimerTask() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    /**
     * 销毁本view
     */
    private void removeFloatView() {
        mWindowManager.removeView(this);
    }


    /**
     * 销毁float view
     */
    public void destroy() {
        hide();
        removeFloatView();
        removeTimerTask();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimeHandler.removeMessages(1);
    }


    /**
     * 切换为横屏的时候
     *
     * @param newConfig
     */
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

        Log.i("TAG", FloatView.class + ",onConfigurationChanged,横屏之后的宽：" + mScreenWidth + ",高：" + mScreenHeight);
        // 其实旋转屏幕之后，在init()方法已经更新了 mWmParams.x和mWmParams.y了，所以这里的x和y其实是当前屏幕方向的信息了
        int oldX = mWmParams.x;
        int oldY = mWmParams.y;
        Log.i("TAG", FloatView.class + ",onConfigurationChanged,以前的高：" + oldY);

        switch (newConfig.orientation) {

            case Configuration.ORIENTATION_LANDSCAPE: // 横屏
                if(mIsRight){
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                }else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;

            case Configuration.ORIENTATION_PORTRAIT : // 竖屏
                if(mIsRight){
                    mWmParams.x = mScreenHeight;
                    mWmParams.y = oldY;
                }else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
        }
        mWindowManager.updateViewLayout(this,mWmParams);
    }
}
