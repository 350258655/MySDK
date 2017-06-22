package action.hdsdk.com.sdk.cyrpt;

import android.text.TextUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class DES {
	public static final String DECIPHERINGAES_KEY = "consummateadvert";
	public static final String DECIPHERING_CONTENT = "CC9Q2fnHFm0TrX7APPHEkid7PqIdkTlgNTWLphP7nK/odLAUwOjcztTXKLl2 gOIC";
	public static final String DECIPHERING_KEY = "winclickledian123654@@@@####****!!!!";
	private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

	public static String encryptDES(String encryptString/*, String encryptKey*/) {
		String encryptKey = DECIPHERING_KEY;
		if (! TextUtils.isEmpty(encryptKey)) {
			encryptKey = getKey(encryptKey);

			IvParameterSpec zeroIv = new IvParameterSpec(iv);
			SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
			try {
				Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
				cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
				byte[] encryptedData = cipher.doFinal(encryptString.getBytes("utf-8"));
				return Base64.encode(encryptedData);
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
		return "";
	}

	public static String getKey(String decryptKey) {
		String key = null;
		if (! TextUtils.isEmpty(decryptKey)) {
			int length = decryptKey.length();
			if (length > 8) {
				key = decryptKey.substring(0, 8);
			} else {
				int s = 8 - length;// 相差多少个字�?
				StringBuilder sb = new StringBuilder(decryptKey);
				for (int i = 0; i < s; i++) {
					sb.append("*");
				}
				key = sb.toString();
			}
		}
		return key;
	}
	
	public static String decryptDES(String decryptString/*, String decryptKey*/) {
		String decryptKey = DECIPHERING_KEY;
		decryptKey = getKey(decryptKey);
		byte[] byteMi = Base64.decode(decryptString);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
			byte decryptedData[] = cipher.doFinal(byteMi);
			return new String(decryptedData,"utf-8");
		} catch (Exception e) {
		}
		return "";
	}
	public static byte[] encryptDESBytes(String encryptString/*, String encryptKey*/) {
		String encryptKey = DECIPHERING_KEY;
		encryptKey = getKey(encryptKey);
		if (! TextUtils.isEmpty(encryptKey)) {
			IvParameterSpec zeroIv = new IvParameterSpec(iv);
			SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
			try {
				Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
				cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
				byte[] encryptedData = cipher.doFinal(encryptString.getBytes("utf-8"));
				return encryptedData;
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
		return null;
	}

	public static String decryptDESBytes(byte[] byteMi/*, String decryptKey*/) {
		String decryptKey = DECIPHERING_KEY;
		decryptKey = getKey(decryptKey);
		try {
			IvParameterSpec zeroIv = new IvParameterSpec(iv);
			SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
			byte decryptedData[] = cipher.doFinal(byteMi);
			return new String(decryptedData, "utf-8");
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return "";
	}
}