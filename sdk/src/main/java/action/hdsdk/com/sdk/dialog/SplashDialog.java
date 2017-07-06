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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import action.hdsdk.com.sdk.R;
import action.hdsdk.com.sdk.utils.BitmapUtils;
import action.hdsdk.com.sdk.utils.MetaDataUtils;
import action.hdsdk.com.sdk.utils.Utils;

/**
 * Created by xk on 2017/6/19.
 */
public class SplashDialog extends Dialog {
    private Context mContext;
    private ImageView image_splash;
    private Handler mHandler;
    private int hdorientation = 0;
    private int width;  // 屏幕宽
    private int height;  // 屏幕高
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // 让splash消失
            cancel();
        }
    };


    public SplashDialog(Context context) {
        super(context, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        this.mContext = context;
        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 背景色透明
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }

        setContentView(R.layout.hd_splash);
        image_splash = findViewById(R.id.image_splash);

        // 判断横竖屏
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        // 横屏
        if (width > height) {
            hdorientation = 1;
            initBitmap(context, "horizontalSplash.png");
        } else {
            initBitmap(context, "verticalSplash.png");
        }


        // 初始化Handler
        mHandler = new Handler();

    }

    /**
     * 初始化splash的图片
     */
    private void initBitmap(Context context, String bitmapName) {
        // Bitmap bitmap = null;
        try {
            InputStream is = context.getResources().getAssets().open(bitmapName);
            //   bitmap = BitmapFactory.decodeStream(is);

            Bitmap bitmap = BitmapUtils.decodeBitmapFromStream(is, width, height);

            image_splash.setImageBitmap(bitmap);
            if (hdorientation == 1) {
                image_splash.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 显示splash
     */
    public void show() {
        super.show();
        // 显示2秒
        mHandler.postDelayed(mRunnable, 2000);
    }

    /**
     * 让splash消失
     */
    @Override
    public void cancel() {
        mHandler.removeCallbacks(mRunnable);
        if (this.isShowing()) {
            super.cancel();
        }
    }
}
