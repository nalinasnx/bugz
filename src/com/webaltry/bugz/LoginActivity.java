package com.webaltry.bugz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends Activity {
	
	private static final String PREFERENCE_SERVER = "LoginActivity.server";
	private static final String PREFERENCE_USER = "LoginActivity.username";
	private static final String PREFERENCE_PASSWORD = "LoginActivity.password";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
			
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		// addPreferencesFromResource(R.xml.login_prefs);

		TextView titleCaption = (TextView) findViewById(R.id.title_caption);
		titleCaption.setText("Connection");
		final Button titleButton = (Button) findViewById(R.id.title_button);
		titleButton.setText("Connect");
		
		
		/* populate inputs from preferences */

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(LoginActivity.this);

		String value = prefs.getString(PREFERENCE_SERVER, "");
		if (value != null) {
			TextView field = (TextView) findViewById(R.id.loginServer);
			field.setText(value);
		}
		
		value = prefs.getString(PREFERENCE_USER, "");
		if (value != null) {
			TextView field = (TextView) findViewById(R.id.loginUser);
			field.setText(value);
		}
		
		value = prefs.getString(PREFERENCE_PASSWORD, "");
		if (value != null) {
			TextView field = (TextView) findViewById(R.id.loginPassword);
			field.setText(value);
		}
		
		
		titleButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				/* get values from form */
				TextView field = (TextView) findViewById(R.id.loginServer);
				String loginServer = field.getText().toString();
								
				field = (TextView) findViewById(R.id.loginUser);
				String loginUser = field.getText().toString();
				
				field = (TextView) findViewById(R.id.loginPassword);
				String loginPassword = field.getText().toString();
				
				
				/* save the values */
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(LoginActivity.this);
				
				SharedPreferences.Editor edit = prefs.edit();
				
				edit.putString(PREFERENCE_SERVER, loginServer);
				edit.putString(PREFERENCE_USER, loginUser);
				edit.putString(PREFERENCE_PASSWORD, loginPassword);
				
				edit.commit();
				
				BugzillaApplication app = (BugzillaApplication) getApplication();
				ConnectTask task = new ConnectTask(app.getBugzilla(),
						loginServer, loginUser, loginPassword);
				task.execute("");
			}
		});
	}

	private void onPostConnect() {

		BugzillaApplication app = (BugzillaApplication) getApplication();
		Bugzilla bugz = app.getBugzilla();

		if (!bugz.isConnected()) {
			
			String message = "Failed to connect to bugzilla server";
			String bugzMessage = bugz.getErrorMessage();
			if (bugzMessage != null) {
				message += ":\n\n";
				message += bugzMessage;
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Bugzilla");
			builder
			.setMessage(message)
			.setCancelable(false)
			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					
				}
			  });
			
			AlertDialog alert = builder.create();

			alert.show();
		} else {
			finish();
		}
	}

	private class ConnectTask extends AsyncTask<String, Void, String> {

		// private Exception exception;
		private Bugzilla bugzilla;
		private String loginServer;
		private String loginUser;
		private String loginPassword;
		private ProgressDialog waitDialog;

		public ConnectTask(Bugzilla bugzilla, String loginServer,
				String loginUser, String loginPassword) {
			this.bugzilla = bugzilla;
			this.loginServer = loginServer;
			this.loginUser = loginUser;
			this.loginPassword = loginPassword;
		}

		@Override
		protected String doInBackground(String... queryString) {

			String results = "fail";

			bugzilla.connect(loginServer, loginUser, loginPassword);
			results = "connected";

			return results;
		}

		@Override
		protected void onPreExecute() {

			waitDialog = ProgressDialog.show(LoginActivity.this,
					"Please wait...", "Connecting...", true, true);

			waitDialog
					.setOnCancelListener(new ConnectTaskOnCancelListener(this));
		}

		@Override
		protected void onPostExecute(String results) {

			// close the wait dialog
			runOnUiThread(new Runnable() {
				// @Override
				public void run() {
					if (waitDialog != null) {
						waitDialog.dismiss();
						waitDialog = null;
					}
				}
			});

			// exit the login activity
			onPostConnect();
		}
	}

	private class ConnectTaskOnCancelListener implements OnCancelListener {

		private AsyncTask<?, ?, ?> task;

		public ConnectTaskOnCancelListener(AsyncTask<?, ?, ?> task) {
			this.task = task;
		}

		// @Override
		public void onCancel(DialogInterface dialog) {
			if (task != null) {
				task.cancel(true);
			}
		}
	}

}
