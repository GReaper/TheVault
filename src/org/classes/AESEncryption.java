package org.classes;

import java.security.AlgorithmParameters;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

/**
 *  This class encrypts the given content using AES 256, CBC and PKCS5 padding.
 *  
 *  IMPORTANT: beware when making changes on this class. Switching between strings and
 *  bytes[] or other functions may alter result's padding and this will cause the 
 *  encryption system to fail.
 * 
 * @author Javier López
 * @version 1.0
 *
 */
public class AESEncryption 
{
	// String to be used for encryption/decryption tests
	private static String key = "op387xdjk28querl97cmn19aszpetikj";
	
	/**
	 * This function allow to decrypt the given content using the method described above.
	 * 
	 * @author Javier López
	 * @version 1.0
	 * 
	 * @param encrypted. String with the content to be decrypted
	 * @return String with the decrypted content
	 * @throws Exception. In case of invalid data/decryption error
	 */
	public static String decrypt(String encrypted) throws Exception 
	{		
		// Base64 decode encrypted string (using UTF-8) and extract bytes array
	    byte[] decodedBytes = Base64.decode(encrypted.getBytes("UTF-8"), Base64.DEFAULT);
	    
	    // Get IV bytes 
		byte[] iv = new byte[16];
		System.arraycopy(decodedBytes,0,iv,0,iv.length);	
		// Create IV parameter using IV bytes
		IvParameterSpec	ivspec = new IvParameterSpec(iv);
		
		// Get content to decrypt
		byte[] toDecrypt = new byte[decodedBytes.length-16];
		System.arraycopy(decodedBytes,16,toDecrypt,0,toDecrypt.length);	
		
		// Create secret key using its bytes (using UTF-8). Key marked as AES
	    byte[] keyb = key.getBytes("UTF-8");
	    SecretKeySpec skey = new SecretKeySpec(keyb, "AES");
	    
	    // Create AES cipher object
	    Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    // Initialize cipher with all params and with decryption mode
	    dcipher.init(Cipher.DECRYPT_MODE, skey, ivspec);
	    
	    // Decrypt text with cipher object and return result as string
	    return new String(dcipher.doFinal(toDecrypt));
	}
	
	/**
	 * This function allow to decrypt the given content using the method described above.
	 * It decrypts to bytes array.
	 * 
	 * @author Javier López
	 * @version 1.0
	 * 
	 * @param encrypted. String with the content to be decrypted
	 * @return byte[] with the decrypted content
	 * @throws Exception. In case of invalid data/decryption error
	 */
	public static byte[] decryptToBytes(String encrypted) throws Exception 
	{		
		// Base64 decode encrypted string (using UTF-8) and extract bytes array
	    byte[] decodedBytes = Base64.decode(encrypted.getBytes("UTF-8"), Base64.DEFAULT);
	    
	    // Get IV bytes 
		byte[] iv = new byte[16];
		System.arraycopy(decodedBytes,0,iv,0,iv.length);	
		// Create IV parameter using IV bytes
		IvParameterSpec	ivspec = new IvParameterSpec(iv);
		
		// Get content to decrypt
		byte[] toDecrypt = new byte[decodedBytes.length-16];
		System.arraycopy(decodedBytes,16,toDecrypt,0,toDecrypt.length);	
		
		// Create secret key using its bytes (using UTF-8). Key marked as AES
	    byte[] keyb = key.getBytes("UTF-8");
	    SecretKeySpec skey = new SecretKeySpec(keyb, "AES");
	    
	    // Create AES cipher object
	    Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    // Initialize cipher with all params and with decryption mode
	    dcipher.init(Cipher.DECRYPT_MODE, skey, ivspec);
	    
	    // Decrypt text with cipher object and return result as string
	    return dcipher.doFinal(toDecrypt);
	}
	
	/**
	 * This function allow to decrypt the given content using the method described above.
	 * It decrypts from bytes array to bytes array.
	 * 
	 * @author Javier López
	 * @version 1.0
	 * 
	 * @param encrypted. byte[] with the content to be decrypted
	 * @return byte[] with the decrypted content
	 * @throws Exception. In case of invalid data/decryption error
	 */
	public static byte[] decryptFromBytesToBytes(byte[] encrypted) throws Exception 
	{		
		// Base64 decode encrypted string (using UTF-8) and extract bytes array
	    //byte[] decodedBytes = Base64.decode(encrypted, Base64.DEFAULT);
	    byte[] decodedBytes = encrypted;
	    
	    // Get IV bytes 
		byte[] iv = new byte[16];
		System.arraycopy(decodedBytes,0,iv,0,iv.length);	
		// Create IV parameter using IV bytes
		IvParameterSpec	ivspec = new IvParameterSpec(iv);
		
		// Get content to decrypt
		byte[] toDecrypt = new byte[decodedBytes.length-16];
		System.arraycopy(decodedBytes,16,toDecrypt,0,toDecrypt.length);	
		
		// Create secret key using its bytes (using UTF-8). Key marked as AES
	    byte[] keyb = key.getBytes("UTF-8");
	    SecretKeySpec skey = new SecretKeySpec(keyb, "AES");
	    
	    // Create AES cipher object
	    Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    // Initialize cipher with all params and with decryption mode
	    dcipher.init(Cipher.DECRYPT_MODE, skey, ivspec);
	    
	    // Decrypt text with cipher object and return result as string
	    return dcipher.doFinal(toDecrypt);
	}
	
	/**
	 * This function encrypts the given content using the method described above.
	 * 
	 * @author Javier López
	 * @version 1.0
	 * 
	 * @param content. String with the content to be encrypted
	 * @return String with the encrypted content
	 * @throws Exception In case of invalid data/encryption error
	 */
	public static String encrypt(String content) throws Exception 
	{	
		// Create secret key using its bytes (using UTF-8). Key marked as AES
		byte[] keyb = key.getBytes("UTF-8");
	    SecretKeySpec skey = new SecretKeySpec(keyb, "AES");
	    
	    // Create AES cipher object
	    Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    // Initialize cipher with all params and encryption mode
	    dcipher.init(Cipher.ENCRYPT_MODE, skey);
	    
	    // Get generated IV
	    AlgorithmParameters params = dcipher.getParameters();
		// Get IV bytes
	    byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

	    // Encrypt text and retrieve result bytes (using UTF-8)
	    byte[] result = dcipher.doFinal(content.getBytes("UTF-8"));
	    
	    // Append IV to the encrypted content
	    byte[] eresult = new byte[iv.length + result.length];
		// copy a to result
		System.arraycopy(iv, 0, eresult, 0, iv.length);
		// copy b to result
		System.arraycopy(result, 0, eresult, iv.length, result.length);

	    // Return string with the base64 form of the encrypted text  
	    return Base64.encodeToString(eresult, Base64.DEFAULT);
	}
	
	/**
	 * This function encrypts the given content (as bytes array) using the 
	 * method described above.
	 * 
	 * @author Javier López
	 * @version 1.0
	 * 
	 * @param bArray. Bytes array with the content to be encrypted
	 * @return String with the encrypted content
	 * @throws Exception In case of invalid data/encryption error
	 */
	public static String encryptBytes(byte[] bArray) throws Exception 
	{	
		// Create secret key using its bytes (using UTF-8). Key marked as AES
		byte[] keyb = key.getBytes("UTF-8");
	    SecretKeySpec skey = new SecretKeySpec(keyb, "AES");
	    
	    // Create AES cipher object
	    Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    // Initialize cipher with all params and encryption mode
	    dcipher.init(Cipher.ENCRYPT_MODE, skey);
	    
	    // Get generated IV
	    AlgorithmParameters params = dcipher.getParameters();
		// Get IV bytes
	    byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

	    // Encrypt text and retrieve result bytes (using UTF-8)
	    byte[] result = dcipher.doFinal(bArray);
	    
	    // Append IV to the encrypted content
	    byte[] eresult = new byte[iv.length + result.length];
		// copy a to result
		System.arraycopy(iv, 0, eresult, 0, iv.length);
		// copy b to result
		System.arraycopy(result, 0, eresult, iv.length, result.length);

	    // Return string with the base64 form of the encrypted text  
	    return Base64.encodeToString(eresult, Base64.DEFAULT);
	}
	
	/**
	 * This function encrypts the given content (as bytes array) using the 
	 * method described above.
	 * 
	 * @author Javier López
	 * @version 1.0
	 * 
	 * @param bArray. Bytes array with the content to be encrypted
	 * @return byte[] with the encrypted content
	 * @throws Exception In case of invalid data/encryption error
	 */
	public static byte[] encryptFromBytesToBytes(byte[] bArray) throws Exception 
	{	
		// Create secret key using its bytes (using UTF-8). Key marked as AES
		byte[] keyb = key.getBytes("UTF-8");
	    SecretKeySpec skey = new SecretKeySpec(keyb, "AES");
	    
	    // Create AES cipher object
	    Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    // Initialize cipher with all params and encryption mode
	    dcipher.init(Cipher.ENCRYPT_MODE, skey);
	    
	    // Get generated IV
	    AlgorithmParameters params = dcipher.getParameters();
		// Get IV bytes
	    byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

	    // Encrypt text and retrieve result bytes (using UTF-8)
	    byte[] result = dcipher.doFinal(bArray);
	    
	    // Append IV to the encrypted content
	    byte[] eresult = new byte[iv.length + result.length];
		// copy a to result
		System.arraycopy(iv, 0, eresult, 0, iv.length);
		// copy b to result
		System.arraycopy(result, 0, eresult, iv.length, result.length);

	    // Return byte[] with the base64 form of the encrypted text  
	    //return Base64.encodeToString(eresult, Base64.DEFAULT).getBytes("UTF-8");
	    return eresult;
	}
}