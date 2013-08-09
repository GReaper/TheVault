package org.v1.thevault;

import java.util.ArrayList;
import java.util.Collections;

import org.classes.PreferencesManager;
import org.v1.thevault.locked.FolderVisualizer;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity 
{
	// Key length
	public final static int KEY_LENGTH = 4;
	
	// String with the key to be tested
	private String input_key = "";
	
	// Other attrs.
	PreferencesManager pManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		// Create preferences manager
		pManager = new PreferencesManager(this);
		
		if (pManager.getPassword().equals(""))
		{
			// Password not set. Show the register screen
			initRegisterScreen();					
		}
		else
		{
			// Password set. Show the login screen
			initLoginScreen();			
		}
	}
	
	/**
	 * This function shows the login screen
	 */
	private void initLoginScreen()
	{
		// Set content view
		setContentView(R.layout.login_activity_layout);
		
		// Get buttons
		Button b1 = (Button) findViewById(R.id.pwd_button_1);
		Button b2 = (Button) findViewById(R.id.pwd_button_2);
		Button b3 = (Button) findViewById(R.id.pwd_button_3);
		Button b4 = (Button) findViewById(R.id.pwd_button_4);
		Button b5 = (Button) findViewById(R.id.pwd_button_5);
		Button b6 = (Button) findViewById(R.id.pwd_button_6);
		Button b7 = (Button) findViewById(R.id.pwd_button_7);
		Button b8 = (Button) findViewById(R.id.pwd_button_8);
		Button b9 = (Button) findViewById(R.id.pwd_button_9);
		
		// Assign random numbers between 1 and 9 to every button
		ArrayList<Button> buttons_array = new ArrayList<Button>();
		buttons_array.add(b1);
		buttons_array.add(b2);
		buttons_array.add(b3);
		buttons_array.add(b4);
		buttons_array.add(b5);
		buttons_array.add(b6);
		buttons_array.add(b7);
		buttons_array.add(b8);
		buttons_array.add(b9);
		
		// Shuffle elements
		Collections.shuffle(buttons_array);
		
		// Assign numbers and listeners
		for (int i = 1; i<= buttons_array.size(); i++)
		{
			buttons_array.get(i-1).setText(String.valueOf(i));
			buttons_array.get(i-1).setOnClickListener(new DigitOnClickListener(String.valueOf(i)));
		}
		
		// Assign forgot button listener
		((Button) findViewById(R.id.login_forgot_button)).setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				// Launch forgot activity
			    Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
			    startActivity(intent);	
			    // Finish login activity
			    finish();
			}
		});
	}
	

	/**
	 * This function shows the register screen
	 */
	private void initRegisterScreen()
	{
		// Set content view
		setContentView(R.layout.register_layout);
		
		// Get EditText elements
		final EditText uKey = (EditText) findViewById(R.id.password_text);
		final EditText uQuestion = (EditText) findViewById(R.id.question_text);
		final EditText uAnswer = (EditText) findViewById(R.id.answer_text);
		
		// Get save button and assign listener
		Button bSave = (Button) findViewById(R.id.register_save_button);
		bSave.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				// Password regex
				String regex = "^[1-9]{4}$";
						
				// Get texts
				String sKey = uKey.getText().toString();
				String sQuestion = uQuestion.getText().toString();
				String sAnswer = uAnswer.getText().toString();
				
				// Check fields
				if ((sKey.length() == 0) || (sQuestion.length() == 0) || (sAnswer.length() == 0))
				{
					// Show toast and return
					Toast.makeText(LoginActivity.this, getResources().getString(R.string.empty_data), Toast.LENGTH_SHORT).show();
					return;
				}
				
				// Validate key
				if (!sKey.matches(regex))
				{
					// Show toast and return
					Toast.makeText(LoginActivity.this, getResources().getString(R.string.invalid_key), Toast.LENGTH_SHORT).show();
					return;
				}
				
				// All filters passed. Store all data
				pManager.setPassword(sKey);
				pManager.setSecretQuestion(sQuestion);
				pManager.setSecretAnswer(sAnswer);
				
				// Relaunch activity
			    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
			    startActivity(intent);	
			    // Finish the current one
			    finish();
			}
		});
	}
	
	/**
	 * This OnClickListener is used to manage all digits listeners
	 * 
	 * @author Reaper
	 * @version 1.0
	 */
	private class DigitOnClickListener implements OnClickListener
	{
		private String digit;

		/**
		 * Class constructor
		 * 
		 * @param digit String with the selected digit
		 */
		public DigitOnClickListener(String digit)
		{
			this.digit = digit;
		}
		
		@Override
		public void onClick(View v) 
		{
			input_key += digit;
			if (input_key.length() == KEY_LENGTH)
			{
				// Check for key
				if (pManager.checkPassword(input_key))
				{
					Intent intencion= new Intent(LoginActivity.this,FolderVisualizer.class);
					
					startActivity(intencion);
					finish();
					
					// TODO: start gallery activity
					//Toast.makeText(LoginActivity.this, "Ok!", Toast.LENGTH_SHORT).show();
				}
				else
				{
					// Key error toast
					Toast.makeText(LoginActivity.this, getResources().getString(R.string.key_error), Toast.LENGTH_SHORT).show();					
				}
				
				// Restart key
				input_key = "";
			}
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		return true;
	}

}
