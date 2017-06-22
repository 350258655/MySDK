package action.hdsdk.com.sdk.cyrpt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public class MD5 {
	
	/**
	 * MD5 加密 
	 */
	public static String md5(String value) {
		String result = null;
		if (value != null && value.length() > 0) {
			try {
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				md5.update(value.getBytes(), 0, value.length());
				result = String.format("%032X", new BigInteger(1, md5.digest()));
			} catch (Exception e) {
				// Not perfect, but trim to 32 chars.
				result = result.substring(0, 32);
			}
		}
		return result;
	}
	
	public static String md5File(File file) throws IOException {
		String result = null;
		if (file.exists()) {
			try {
				@SuppressWarnings("resource")
				FileInputStream in = new FileInputStream(file);
				FileChannel ch =in.getChannel();
				MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				md5.update(byteBuffer);
				result = String.format("%032X", new BigInteger(1, md5.digest()));
			} catch (Exception e) {
				result = result.substring(0, 32);
			}
		}
		return result;
	}
}
