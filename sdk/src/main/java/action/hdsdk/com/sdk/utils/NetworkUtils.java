package action.hdsdk.com.sdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络APN处理(创建与关闭APN)
 * 
 * @author Windale
 * @created 2011-3-11
 * @last 2011-3-11
 */
public class NetworkUtils {
	

	
	/**
	 * 得到网络连接类型
	 * @return ConnectType
	 */
	public static String getConnectionType(Context context) {
		if (null != context) {
			ConnectivityManager manager = createConnectivityManager(context);
			if (null != manager) {
				NetworkInfo info = manager.getActiveNetworkInfo();
				if (null != info) {
					if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
						return info.getExtraInfo().toLowerCase(); 
					} else if (info.getType() == ConnectivityManager.TYPE_WIFI){
						return "wifi";
					}
				}
			}
		}
		return "";
	}

	/**
	 * 创建网络连接管理对象
	 *
	 */
	private static ConnectivityManager createConnectivityManager(Context context) {
		return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

}
