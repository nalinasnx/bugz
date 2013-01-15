
package com.webaltry.bugz;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class QueryResultsActivity extends ListActivity {

    private static final String TAG = QueryResultsActivity.class.getSimpleName();
    public static final String QUERY_ID = "QUERY_ID";
    private long mQueryId = 0;
    private BroadcastReceiver mRequestReceiver;

    private enum Status {
        UNINITIALIZED, QUERY_PENDING, QUERY_SUCCEEDED, QUERY_FAILED
    }

    private Status mStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.bugs);

        Log.d(TAG, "onCreate");

        setStatus(Status.UNINITIALIZED);

        mQueryId = getIntent().getLongExtra(QUERY_ID, 0);

        ContentResolver resolver = this.getContentResolver();
        
        Cursor cursor = resolver.query(
                ContentUris.withAppendedId(BugzillaProvider.URI_GET_ALL_BUGS_OF_QUERY, mQueryId), null,
                null, null, null);
      
        startManagingCursor(cursor);
        ListAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.two_line_list_item,
                cursor,
                new String[] {
                        BugzillaDatabase.FIELD_NAME_BUG_ID, BugzillaDatabase.FIELD_NAME_SUMMARY
                },
                new int[] {
                        android.R.id.text1, android.R.id.text2
                });

        setListAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* get data if it exists */
        Log.d(TAG, "onResume");

        IntentFilter filter = new IntentFilter(BugzillaServiceHelper.TASK_RUN_QUERY);
        mRequestReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.hasExtra(BugzillaServiceHelper.TASK_QUERY_ID))
                {
                    long resultQueryId = intent.getLongExtra(BugzillaServiceHelper.TASK_QUERY_ID, 0);

                    Log.d(TAG, "Received intent " + intent.getAction() + ", query ID "
                            + resultQueryId);

                    if (resultQueryId == mQueryId) {

                        int resultCode = intent.getIntExtra(BugzillaServiceHelper.TASK_RESULT_CODE,
                                0);

                        Log.d(TAG, "Result code = " + resultCode);

                        if (resultCode == 0) {

                            /* show data */
                            Log.d(TAG, "Updating UI with new data");
                            setStatus(Status.QUERY_SUCCEEDED);

                        } else {

                            /* show error */
                            setStatus(Status.QUERY_FAILED);

                            // an error occurred
                            // showToast(getString(R.string.error_occurred));
                        }
                    }
                }
            }
        };

        BugzillaApplication app = (BugzillaApplication) getApplication();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(app);
        broadcastManager.registerReceiver(mRequestReceiver, filter);

        BugzillaServiceHelper helper = app.getBugzillaServiceHelper();

        if (helper.isQueryRunning(mQueryId)) {
            Log.d(TAG, "Query Running");
            setStatus(Status.QUERY_PENDING);
        } else {
            helper.runQuery(mQueryId);
            setStatus(Status.QUERY_PENDING);
        }
    }

    @Override
    protected void onPause() {

        super.onPause();

        Log.d(TAG, "onPause");

        // Unregister for broadcast
        if (mRequestReceiver != null) {

            try {

                BugzillaApplication app = (BugzillaApplication) getApplication();
                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(app);
                broadcastManager.unregisterReceiver(mRequestReceiver);

            } catch (IllegalArgumentException e) {

                Log.e(TAG, e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * Sets the status of obtaining data from the service, and updates the UI
     * 
     * @param status the new status
     */
    private void setStatus(Status status) {

        mStatus = status;
        TextView message = (TextView) findViewById(android.R.id.empty);

        if (message != null) {

            switch (mStatus) {

                case UNINITIALIZED:
                    message.setText("UNINITIALIZED");
                    break;

                case QUERY_PENDING:
                    message.setText("QUERY_PENDING");
                    break;

                case QUERY_SUCCEEDED:
                    message.setText("QUERY_SUCCEEDED");
                    break;

                case QUERY_FAILED:
                    message.setText("QUERY_FAILED");
                    break;
            }

        }

    }
}
