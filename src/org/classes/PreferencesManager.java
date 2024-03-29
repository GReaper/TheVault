package org.classes;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

/**
 * This class is used to manage all preferences elements
 * 
 * @author Reaper
 * @version 1.0
 */
public class PreferencesManager 
{
	// Preferences strings
	private final String PREFERENCES_NAME = "vaultPreferences";
	private final String PREF_PASSWORD = "vaultPassword";
	//private final String PREF_PASSWORD_HINT = "vaultPasswordHint";
	private final String PREF_SECRET_QUESTION = "vaultSecretQuestion";
	private final String PREF_SECRET_ANSWER = "vaultSecretAnswer";	
	
	private Context context;
	
	/**
	 * Preferences Manager constructor
	 * 
	 * @author Reaper
	 * 
	 * @param context Context of the general application
	 */
	public PreferencesManager(Context context)
	{
		this.context = context;
	}
	
	/**
	 * This function generates and returns the application shared preferences folder. This file
	 * is generated with app read-only permissions.
	 * 
	 * @author Reaper
	 * 
	 * @return SharedPreferences object 
	 */
	public SharedPreferences getPreferences()
	{
		 return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
	}
	
	/* 
	 * Getters and setters for all fields.
	 * Unset fields will be marked with the String value "" 
	 * 
	 */
	
	/**
	 * Function to get the user Vault password. It will be stored as the SHA1 value
	 * of the user selected password.
	 * 
	 * @author Reaper
	 * 
	 * @return String with the user password
	 */
	public String getPassword()
	{
		return getPreferences().getString(PREF_PASSWORD, "");
	}
	
	/**
	 * This function sets a password in preferences file. It stores the
	 * SHA1 value of the given String.
	 * 
	 * @author Reaper
	 * 
	 * @param password
	 */
	public void setPassword(String password)
	{
		try 
		{
			// Generate SHA1 value of the password
			MessageDigest digester = MessageDigest.getInstance("SHA-1");
			digester.reset();
			byte[] sha1hash = digester.digest(password.getBytes());
			
			String enc_pwd = convertToHex(sha1hash);
			
			// Create SharedPreferences editor
			Editor editor = getPreferences().edit();
			// Set password field value
			editor.putString(PREF_PASSWORD, enc_pwd);
			// Commit changes
			editor.commit();
		} 
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Function to get the user Vault password hint. 
	 * 
	 * @author Reaper
	 * 
	 * @return String with the user password hint
	 */
	/*public String getPasswordHint()
	{
		return getPreferences().getString(PREF_PASSWORD_HINT, "");
	}*/
	
	/**
	 * This function sets a password hint in preferences file.
	 * 
	 * @author Reaper
	 * 
	 * @param hint String with the password hint
	 */
	/*public void setPasswordHint(String hint)
	{
		// Create SharedPreferences editor
		Editor editor = getPreferences().edit();
		// Set hint field value
		editor.putString(PREF_PASSWORD_HINT, hint);
		// Commit changes
		editor.commit();
	}*/
	
	/**
	 * Function to get the user Vault secret question. 
	 * 
	 * @author Reaper
	 * 
	 * @return String with the user secret question
	 */
	public String getSecretQuestion()
	{
		return getPreferences().getString(PREF_SECRET_QUESTION, "");
	}
	
	/**
	 * This function sets the secret question in preferences file.
	 * 
	 * @author Reaper
	 * 
	 * @param question String with the secret question
	 */
	public void setSecretQuestion(String question)
	{
		// Create SharedPreferences editor
		Editor editor = getPreferences().edit();
		// Set question field value
		editor.putString(PREF_SECRET_QUESTION, question);
		// Commit changes
		editor.commit();
	}
	
	/**
	 * Function to get the user Vault secret answer. 
	 * 
	 * @author Reaper
	 * 
	 * @return String with the user secret answer
	 */
	public String getSecretAnswer()
	{
		return getPreferences().getString(PREF_SECRET_ANSWER, "");
	}
	
	/**
	 * This function sets the secret answer in preferences file. It is converted
	 * to SHA1.
	 * 
	 * @author Reaper
	 * 
	 * @param answer String with the secret answer
	 */
	public void setSecretAnswer(String answer)
	{
		try
		{
			// Generate SHA1 value of the password
			MessageDigest digester = MessageDigest.getInstance("SHA-1");
			digester.reset();
			byte[] sha1hash = digester.digest(answer.getBytes());
			
			String enc_anser = convertToHex(sha1hash);
			
			// Create SharedPreferences editor
			Editor editor = getPreferences().edit();
			// Set question field value
			editor.putString(PREF_SECRET_ANSWER, enc_anser);
			// Commit changes
			editor.commit();
		} 
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/* Aux. functions */
	
	/**
	 * This function converts the given byte array on a Hex. String
	 * 
	 * @author Reaper
	 * 
	 * @param data byte[] to be converted
	 * @return String with the resulting hex. value
	 * @throws java.io.IOException
	 */
	private static String convertToHex(byte[] data) throws java.io.IOException
    {        
		StringBuffer sb = new StringBuffer();
		String hex = null;    
		hex = Base64.encodeToString(data, 0, data.length, 0);    
		sb.append(hex);                
		return sb.toString();
	}
	
	/**
	 * This function checks if the given password (not hashed) matches the
	 * saved one.
	 * 
	 * @author Reaper
	 * 
	 * @param pwd String with an unhashed password
	 * @return Boolean indicating if the given password is a valid one
	 */
	public boolean checkPassword(String pwd)
	{
		// Get stored password
		String stored_pwd = getPassword();
		
		// If password hasn't been set, return false for security reasons
		if (stored_pwd.equals(""))
		{
			return false;
		}
		
		// Convert the given string to SHA1 and check
		try
		{
			// Generate SHA1 value of the password
			MessageDigest digester = MessageDigest.getInstance("SHA-1");
			digester.reset();
			byte[] sha1hash = digester.digest(pwd.getBytes());
			
			String enc_pwd = convertToHex(sha1hash);
			
			// Check passwords
			if (enc_pwd.equals(stored_pwd))
			{
				return true;
			}
			else
			{
				return false;
			}
		} 
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
			return false;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * This function checks if the given secret answer (not hashed) matches the
	 * saved one.
	 * 
	 * @author Reaper
	 * 
	 * @param pwd String with an unhashed secret answer
	 * @return Boolean indicating if the given anser is a valid one
	 */
	public boolean checkSecretAnswer(String answer)
	{
		// Get stored answer
		String stored_answer = getSecretAnswer();
		
		// Convert the given string to SHA1 and check
		try
		{
			// Generate SHA1 value of the answer
			MessageDigest digester = MessageDigest.getInstance("SHA-1");
			digester.reset();
			byte[] sha1hash = digester.digest(answer.getBytes());
			
			String enc_answer = convertToHex(sha1hash);
			
			// Check passwords
			if (enc_answer.equals(stored_answer))
			{
				return true;
			}
			else
			{
				return false;
			}
		} 
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
			return false;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
	}
}
