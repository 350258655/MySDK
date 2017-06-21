package action.hdsdk.com.sdk.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by shake on 2017/6/20.
 * 常用的工具方法
 */
public class Utils {


    /**
     * 获取屏幕大小
     *
     * @param context
     * @return
     */
    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return new Point(screenWidth, screenHeight);
    }


    /**
     * 打印日志
     * @param clazz
     * @param msg
     */
    public static void log(Class clazz,String msg){
        Log.i("TAG", clazz.getSimpleName()+" --> "+msg);
    }

}
