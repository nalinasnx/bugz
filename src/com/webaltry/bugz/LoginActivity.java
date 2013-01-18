
package com.webaltry.bugz;

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

public class LoginActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title); 
        addPreferencesFromResource(R.xml.login_prefs);
        
        TextView titleCaption = (TextView)findViewById(R.id.title_caption);
        titleCaption.setText("Connection");
        final Button titleButton = (Button)findViewById(R.id.title_button);
        titleButton.setText("Connect");
        titleButton.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                
                
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(LoginActivity.this);
				
              String loginServer = prefs.getString("connection.server", "");
              String loginUser = prefs.getString("connection.username", "");
              String loginPassword = prefs.getString("connection.password", "");

              BugzillaApplication app = (BugzillaApplication) getApplication();
              ConnectTask task = new ConnectTask(app.getBugzilla(), loginServer, loginUser,
                      loginPassword);
              task.execute("");
            }
        });
    }

    private void onPostConnect() {

        BugzillaApplication app = (BugzillaApplication) getApplication();
        Bugzilla bugz = app.getBugzilla();

        if (!bugz.isConnected()) {
            AlertDialog alert = new AlertDialog.Builder(this).create();
            alert.setTitle("Bugzilla");
            alert.setMessage("Failed to connect to bugzilla server: \n\n" + bugz.getErrorMessage());
        } else {
            finish();
        }
    }

    private class ConnectTask extends AsyncTask<String, Void, String> {

        //private Exception exception;
        private Bugzilla bugzilla;
        private String loginServer;
        private String loginUser;
        private String loginPassword;
        private ProgressDialog waitDialog;

        public ConnectTask(Bugzilla bugzilla, String loginServer, String loginUser,
                String loginPassword) {
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

            waitDialog.setOnCancelListener(new ConnectTaskOnCancelListener(this));
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
