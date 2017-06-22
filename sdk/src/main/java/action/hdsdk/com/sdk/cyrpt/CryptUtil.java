package action.hdsdk.com.sdk.cyrpt;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtil {
	
	 private static final CryptUtil instance = new CryptUtil();   
	  
	    private CryptUtil() {   
	  
	    }   
	  
	    public static CryptUtil getInstance() {   
	        return instance;   
	    }
	  

	    public String encryptAES(String content, String key){
	        try{   
	            if (key == null) {  
	                return null;  
	            }  
	            if (key.length() != 16) {
	                return null;  
	            } 
	        
	        	 byte[] raw = key.getBytes();  
	             SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	             Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
	             IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强�?
	             cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
	             byte[] encrypted = cipher.doFinal(content.getBytes());  
	       
	             return  Base64.encode(encrypted);//此处使用BASE64做转码功能，同时能起�?2次加密的作用�?  
	        }   
	        catch (Exception e){
	            e.printStackTrace();   
	        }   
	        return null;   
	    }   
	  

	    public String decryptAES(String content, String key){
	        try{   
	        	 byte[] raw = key.getBytes("ASCII");  
	             SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	             Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	             IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
	             cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	             byte[] encrypted1 = Base64.decode(content);//先用base64解密  
	             byte[] original = cipher.doFinal(encrypted1);  
	                String originalString = new String(original);
	                return originalString;  
	        }   
	        catch (Exception e){
	            e.printStackTrace();   
	        }   
	        return null;   
	    }   

}
