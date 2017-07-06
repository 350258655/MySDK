package action.hdsdk.com.sdk.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * Created by shake on 2017/7/6.
 */
public class BitmapUtils {


    /**
     * 高效加载Bitmap的工具类
     *
     * @param in
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeBitmapFromStream(InputStream in, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        // 设置 inJustDecodeBounds 属性为true可以在解码的时候避免内存的分配，它会返回一个null的Bitmap，
        // 但是可以获取到 outWidth, outHeight 与 outMimeType。
        // 该技术可以允许你在构造Bitmap之前优先读图片的尺寸与类型。
        options.inJustDecodeBounds = true;

        // 构造Bitmap，读取宽高
        BitmapFactory.decodeStream(in, null, options);

        // 计算inSampleSize，即采样率
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 设置为flase，即接下来再构造Bitmap的时候，不会只是返回null
        options.inJustDecodeBounds = false;

        // 返回的是一个Bitmap对象
        return BitmapFactory.decodeStream(in, null, options);

    }


    /**
     * 计算要压缩的程度
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 获取原本图片的宽高
        int height = options.outHeight;
        int width = options.outWidth;

        Utils.log(BitmapUtils.class, "图片原本的宽：" + width + ",原本的高：" + height);

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize = 2;
            }
        }

        return inSampleSize;
    }
}
