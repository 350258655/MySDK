package action.hdsdk.com.sdk.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by xk on 2017/6/19.
 */
public class MetaDataUtils {

    /**
     * 获取Application节点下的MetaData
     *
     * @param context
     * @param key
     * @return
     */
    public static String getApplicationMetaData(Context context, String key) {
        Object r = null;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (ai != null) {
                r = ai.metaData.get(key);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return r == null ? "" : r.toString();
    }


}
