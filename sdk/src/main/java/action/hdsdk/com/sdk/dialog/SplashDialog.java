package action.hdsdk.com.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import action.hdsdk.com.sdk.R;
import action.hdsdk.com.sdk.utils.MetaDataUtils;
import action.hdsdk.com.sdk.utils.Utils;

/**
 * Created by xk on 2017/6/19.
 */
public class SplashDialog extends Dialog {
    private Context mContext;
    private ImageView image_splash;
    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // 让splash消失
            cancel();
        }
    };


    public SplashDialog(Context context) {
        super(context,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        this.mContext = context;
        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 背景色透明
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置window type
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        }else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }

        setContentView(R.layout.hd_splash);
        image_splash = findViewById(R.id.image_splash);


        // 获取当前屏幕方向
        String hdorientation = MetaDataUtils.getApplicationMetaData(context,"hdorientation");
        Utils.log(SplashDialog.class,"SplashDialog,当前屏幕方向是："+hdorientation);
        // 竖屏
        if(hdorientation.equals("0")){
            initBitmap(context,"verticalSplash.png");
        }else {
        // 横屏
            initBitmap(context,"horizontalSplash.png");
        }

        // 初始化Handler
        mHandler = new Handler();

    }

    /**
     * 初始化splash的图片
     */
    private void initBitmap(Context context,String bitmapName) {
        Bitmap bitmap = null;
        try {
            InputStream is = context.getResources().getAssets().open(bitmapName);
            bitmap = BitmapFactory.decodeStream(is);
            image_splash.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 显示splash
     */
    public void show(){
        super.show();
        // 显示2秒
        mHandler.postDelayed(mRunnable,2000);
    }

    /**
     * 让splash消失
     */
    @Override
    public void cancel() {
        mHandler.removeCallbacks(mRunnable);
        if(this.isShowing()){
            super.cancel();
        }
    }
}
