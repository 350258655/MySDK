package action.hdsdk.com.sdk.utils;

import android.content.Context;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;



public class BaseInfo {
    private static final int TIMEOUT = 60;
    private static final String SAVE_KEY = "BASEINFO";
    private static String baseInfo = null;


    public static void refresh(Context context) {
        try {
            JSONObject _data = new JSONObject();

            _data.put("imei", DeviceUtils.getTelImei(context));
            _data.put("imsi", DeviceUtils.getTelImsi(context));
            _data.put("cpu",DeviceUtils.getCpu(context));
            _data.put("android_id", DeviceUtils.getAndroidId(context));
            _data.put("macaddress", DeviceUtils.getMacAddress(context));
            _data.put("screen_size", DeviceUtils.getScreenValue(context));
            _data.put("screen_inch", DeviceUtils.getPhoneSize(context));
            _data.put("os", "android");
            _data.put("os_version", Build.VERSION.SDK_INT + "");
            _data.put("machina_type", Build.MODEL);
            _data.put("app_ver", AppUtils.getAppVersion(context));
            _data.put("language", DeviceUtils.getPhoneLanguage());
            _data.put("net_type", NetworkUtils.getConnectionType(context));
            _data.put("appid", "585");
            _data.put("appname", AppUtils.getApplicationName(context));
            _data.put("channel_id", "mhzt_01");
            _data.put("packname", context.getPackageName());
            _data.put("sdkver", 1.0);
            _data.put("uuid", AppUtils.getMyUUID(context));
            _data.put("refresh_time", System.currentTimeMillis());
            AppUtils.saveConfig(SAVE_KEY, _data.toString(),context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    public static String getAttributesString(Context context) {
        if (baseInfo == null) {
            baseInfo = AppUtils.getConfig(SAVE_KEY, null,context);

            if (baseInfo == null || baseInfo.isEmpty()) {
                refresh(context);
                baseInfo = AppUtils.getConfig(SAVE_KEY, "{}",context);
            } else {
                JSONObject json;
                try {
                    json = new JSONObject(baseInfo);
                    long timeout = System.currentTimeMillis()
                            - json.getLong("refresh_time");
                    if (timeout > TIMEOUT * 1000) {
                        refresh(context);
                    }

                } catch (JSONException e) {
                    refresh(context);
                }
            }
        }
        return baseInfo;
    }


}
