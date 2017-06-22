package action.hdsdk.com.sdk.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;


public class DeviceUtils {
	
	private static String SCREEN_VALUE = null;
	private static String PHONE_SIZE = null;
	private static String PHONE_LANGUAGE = null;
	private static String IMEI = null;
	private static String IMSI = null;
	private static final String IMSI_ERROR = "310260000000000";
	

	
	/**
	 * Get Screen density
	 */
	public static int getDisplay(Context context ,int value){
		return (int) (value * context.getResources().getDisplayMetrics().density);
	}
	

	public static String getScreenValue(Context context) {
		if (SCREEN_VALUE != null) return SCREEN_VALUE;
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);
		String screen_size = dm.widthPixels + "|" + dm.heightPixels;
		return SCREEN_VALUE = screen_size;
	}


	/**
	 * 根据手机的分辨率�? dp 的单�? 转成�? px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率�? px(像素) 的单�? 转成�? dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	public static String getPhoneWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(
				Context.WINDOW_SERVICE);


		int width = wm.getDefaultDisplay().getWidth();

		return PHONE_SIZE = String.valueOf(width);
	}

	public static String getPhoneHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(
				Context.WINDOW_SERVICE);


		int height = wm.getDefaultDisplay().getHeight();

		return PHONE_SIZE = String.valueOf(height);
	}


	/**
	 * Get Phone inch size
	 */
	public static String getPhoneSize(Context context) {
		if (PHONE_SIZE != null) return PHONE_SIZE;
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);

		// 得到屏幕的宽(像素)
		int screenX = dm.widthPixels;
		// 得到屏幕的高(像素)
		int screenY = dm.heightPixels;

		// 每英寸的像素�?
		int dpi = dm.densityDpi;
		// 得到屏幕的宽(英寸)
		float a = screenX / dpi;
		// 得到屏幕的高(英寸)
		float b = screenY / dpi;

		// 勾股定理
		return PHONE_SIZE = String.valueOf(Math.sqrt((a * a) + (b * b)));
	}
	
	/**
	 * Get Phone OS Version 
	 * 
	 * created: 	2010-10-22 
	 * last modify: 2010-10-22 
	 * author: 		liangxiaoshan
	 */
	public static String getOs_version() {
		return Build.VERSION.RELEASE.substring(0, 3);
	}
	
	/**
	 * Get Phone Language 
	 * 
	 * created: 2010-10-22 
	 * last modify: 2010-10-22 
	 * author：liangxiaoshan
	 */
	public static String getPhoneLanguage() {
		if (PHONE_LANGUAGE != null) return PHONE_LANGUAGE;
		final Locale l = Locale.getDefault();
		return PHONE_LANGUAGE = l.getLanguage();
	}
	
	/**
	 * Get Telephone IMEI
	 * 
	 * @param context
	 * @return String DeviceUtils.java 
	 * create time: 2010-11-24 
	 * last modify: 2010-11-24 
	 * author: liangxiaoshan
	 */
	public static String getTelImei(Context context) {
		if (IMEI != null) return IMEI;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();

		if(imei==null)
		{
			// android pad
			imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		}

		if("000000000000000".equals(imei) || "sdk".equalsIgnoreCase(Build.MODEL)){
			imei= "000000";
		}
		return IMEI = (TextUtils.isEmpty(imei) ? "" : imei);
	}
	/**
	 * 获取cpu型号
	 */
	public static String getCpu(Context context) {
		try{
			FileReader fr = new FileReader("/proc/cpuinfo");
			BufferedReader br = new BufferedReader(fr);
			String text = br.readLine();
			String[] array = text.split(":\\s+",2);
			for(int i = 0; i < array.length; i++){
			}
			return array[1];

		}catch (Exception e){
			e.printStackTrace();
		}
		return "没有CPU型号";

	}
	/**
	 * Get Telephone IMSI
	 */
	public static String getTelImsi(Context context) {
		if (IMSI != null) return IMSI;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		if (TextUtils.isEmpty(imsi)) imsi = "";
		if (imsi.length() > 15) {
			imsi = imsi.substring(0, 15);
		}
		return IMSI = imsi;
	}
	
	/**
	 * Get SDCard is mounted state or not.
	 */
	public static boolean haveSdcard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * Get SDCard Available Space more than X MB
	 */
	public static boolean isAvailableSpace(int sizeMb) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String sdcard = Environment.getExternalStorageDirectory().getPath();
			StatFs statFs = new StatFs(sdcard);
			long blockSize = statFs.getBlockSize();
			long blocks = statFs.getAvailableBlocks();
			long availableSpare = (blocks * blockSize) / (1024 * 1024);
			if (sizeMb > availableSpare) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	

	/**
	 * Get Machine is emulator or not.
	 */
	public static boolean isAnalogMachine(Context context) {
		String imei = DeviceUtils.getTelImei(context);
		String model = Build.MODEL;
		String imsi = DeviceUtils.getTelImsi(context);
		if ("000000000000000".equals(imei) 
				|| "sdk".equalsIgnoreCase(model)
				|| IMSI_ERROR.equals(imsi)) {
			return true;
		}
		return false;
	}

	
	public static String getMacAddress(Context context) {

		String macAddress = "";
		WifiManager wifiMgr = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		if (null != info) {
			macAddress = info.getMacAddress();
		}
		if (StringUtils.isBlank(macAddress)) {
			OpenWifi(wifiMgr);
			macAddress = wifiMgr.getConnectionInfo().getMacAddress();
			CloseWife(wifiMgr);
		}
		return StringUtils.isBlank(macAddress) ? "" : macAddress;
	}

	private static void OpenWifi(WifiManager mWifiManager) {
		try {
			if (!mWifiManager.isWifiEnabled()) {

				mWifiManager.setWifiEnabled(true);// ��wifi

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private static void CloseWife(WifiManager mWifiManager) {
		try {
			if (mWifiManager.isWifiEnabled()) {

				mWifiManager.setWifiEnabled(false);// �ر�wifi

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	/*
	 * Get Android ID 
	 */
	public static String getAndroidId(Context context) {
		String AndroidId = null;
		AndroidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		return TextUtils.isEmpty(AndroidId) ? "" : AndroidId;
	}
	
	public static String netType(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			String typeName = info.getTypeName().toLowerCase(); // WIFI/MOBILE
			if (("wifi").equals(typeName)) {
				return typeName;
			} else {
				typeName = info.getExtraInfo().toLowerCase();
				return typeName;
			}

		} catch (Exception e) {
			return "unknow";
		}

	}
	
	/**
	 * 获取未安装的APK信息
	 * 
	 * @param context
	 * @param archiveFilePath
	 *            APK文件的路径�?�如�?/sdcard/download/XX.apk
	 */
	public static String getUninatllApkInfo(Context context,
			String archiveFilePath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			String packageName = appInfo.packageName;
			return packageName;
		}
		return "";
	}
	
	/**
	 * 判断下载包是否完�?
	 * @param context
	 * @param archiveFilePath
	 * @return
	 */
	public static boolean ApkIntact(Context context,String archiveFilePath){
		boolean result = false;
	    try {
	    PackageManager pm = context.getPackageManager();
	    PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath,
	    PackageManager.GET_ACTIVITIES);
	    if (info != null) {
	    result = true;
	    }
	    } catch (Exception e) {
	    result = false;
	    }
	    return result;
	    }
}
