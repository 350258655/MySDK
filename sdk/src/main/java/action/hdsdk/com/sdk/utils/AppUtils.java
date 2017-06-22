package action.hdsdk.com.sdk.utils;

import android.content.Context;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.text.TextUtils;


import java.util.*;

import action.hdsdk.com.sdk.cyrpt.DES;

public class AppUtils {
    private final static String TAG = "AppUtils";
    private static final String CONFIG = "config";
    private static String uniqueId = null;
    private static final String SP_ID_UUID = "UUID";
    private static final String UUID = "uuid";
    private static final String APPID = "appid";
    private static final String CID = "cid";
    private static final String VERSION = "version";
    private static final String CODE = "code";
    private static final String CHANNEL_RESULT_SUCCESS = "10000";
    private static String channelId;
    private static String appId;
    private static String appversion;
    public static String ACCESS_TOKEN = "";
    public static String PhoneInfo = "";

    public static String getACCESS_TOKEN() {
        return ACCESS_TOKEN;
    }

    public static void setACCESS_TOKEN(String aCCESS_TOKEN) {
        ACCESS_TOKEN = aCCESS_TOKEN;
    }



    /**
     * 获取版本�?
     *
     * @return 当前应用的版本号
     */
    public static String getAppVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            appversion = info.versionName;

        } catch (Exception e) {
            e.printStackTrace();

        }
        return appversion;
    }

    public static String getMyUUID(Context context) {

        if (!TextUtils.isEmpty(uniqueId)) {
            return uniqueId;
        }
        String ret = null;

        AppUtils.getConfig(SP_ID_UUID, "",context);
        if (TextUtils.isEmpty(ret)) {
            String androidId = DeviceUtils.getAndroidId(context);
            String mac = DeviceUtils.getMacAddress(context);
            String imei = DeviceUtils.getTelImei(context);
            if (!(TextUtils.isEmpty(androidId) || TextUtils.isEmpty(imei) || TextUtils
                    .isEmpty(mac))) {
                java.util.UUID deviceUuid = new UUID(androidId.hashCode(),
                        ((long) mac.hashCode() << 32) | imei.hashCode());
                uniqueId = deviceUuid.toString();
                // InnerSDK.getInstance().getDataManager().set("UUID",
                // uniqueId,"1");
                AppUtils.saveConfig(SP_ID_UUID, uniqueId,context);
            }
        } else {
            uniqueId = ret;
        }
        return uniqueId;
    }



    /**
     * Get AppName
     */
    public static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        String applicationName = "";
        try {
            packageManager = context.getApplicationContext()
                    .getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
            applicationName = (String) packageManager
                    .getApplicationLabel(applicationInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return applicationName;
    }







    /**
     * 在app配置里头读取key得配置信�?
     *
     * @param key
     * @return
     */
    public static String getConfig(String key, String defaultValue,Context context) {
        defaultValue = DES.encryptDES(defaultValue);
        return DES.decryptDES(context.getSharedPreferences(
                CONFIG, Context.MODE_PRIVATE).getString(key,
                defaultValue));
    }



    // 更新某一条配�?
    public static void saveConfig(String key, String value,Context context) {

        saveConfig(key, value, "",context);
    }

    public static void saveConfig(String key, String value, String ex,Context context) {

        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value))
            return;

        // 信息加密
        value = DES.encryptDES(value);

        context.getSharedPreferences(CONFIG,
                Context.MODE_PRIVATE).edit().putString(ex + key, value).commit();
    }



}
