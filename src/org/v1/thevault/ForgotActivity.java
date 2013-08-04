package org.v1.thevault;

import org.classes.PreferencesManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgotActivity extends Activity 
{	
	// Other attrs.
	PreferencesManager pManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		// Create preferences manager
		pManager = new PreferencesManager(this);
		
		// Show the forgot screen
		initForgotScreen();		
	}

	/**
	 * This function shows the forgot screen
	 */
	private void initForgotScreen()
	{
		// Set content view
		setContentView(R.layout.forgot_activity_layout);
		
		// Get EditText elements
		final EditText uKey = (EditText) findViewById(R.id.password_text);
		final EditText uAnswer = (EditText) findViewById(R.id.answer_text);
		
		// Set secret question
		((TextView) findViewById(R.id.question_text)).setText(pManager.getSecretQuestion());
		
		// Get save button and assign listener
		Button bSave = (Button) findViewById(R.id.forgot_save_button);
		bSave.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				// Password regex
				String regex = "^[1-9]{4}$";
						
				// Get texts
				String sKey = uKey.getText().toString();
				String sAnswer = uAnswer.getText().toString();
				
				// Check fields
				if ((sKey.length() == 0) || (sAnswer.length() == 0))
				{
					// Show toast and return
					Toast.makeText(ForgotActivity.this, getResources().getString(R.string.empty_data), Toast.LENGTH_SHORT).show();
					return;
				}
				
				// Validate key
				if (!sKey.matches(regex))
				{
					// Show toast and return
					Toast.makeText(ForgotActivity.this, getResources().getString(R.string.invalid_key), Toast.LENGTH_SHORT).show();
					return;
				}
				
				// Validate secret answer
				if (!pManager.checkSecretAnswer(sAnswer))
				{
					// Show toast and return
					Toast.makeText(ForgotActivity.this, getResources().getString(R.string.forgot_invalid_answer), Toast.LENGTH_SHORT).show();
					return;
				}
				
				// All filters passed. Store new password
				pManager.setPassword(sKey);
				
				// Relaunch login activity
			    Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
			    startActivity(intent);	
			    // Finish the current one
			    finish();
			}
		});

		// Get save button and assign listener
		Button bCancel = (Button) findViewById(R.id.forgot_cancel_button);
		bCancel.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				// Relaunch login activity
			    Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
			    startActivity(intent);	
			    // Finish the current one
			    finish();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		return true;
	}

}
