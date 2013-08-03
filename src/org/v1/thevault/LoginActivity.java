package org.v1.thevault;

import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends Activity 
{
	// Key length
	public final static int KEY_LENGTH = 4;
	
	// String with the key to be tested
	private String input_key = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity_layout);
		
		// Init view elements
		initLoginScreen();
	}
	
	private void initLoginScreen()
	{
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
				// TODO: change to user key. Now 1234 is accepted
				if (input_key.equals("1234"))
				{
					// TODO: start gallery activity
					Toast.makeText(LoginActivity.this, "Ok!", Toast.LENGTH_SHORT).show();
				}
				else
				{
					// TODO: show hint
					Toast.makeText(LoginActivity.this, "Hint: ", Toast.LENGTH_SHORT).show();					
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
