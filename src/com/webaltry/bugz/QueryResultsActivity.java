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
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class QueryResultsActivity extends ListActivity {

	private static final String TAG = QueryResultsActivity.class
			.getSimpleName();
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
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.bugs);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title2);

		setStatus(Status.UNINITIALIZED);

		/* get the query id from the intent */
		mQueryId = getIntent().getLongExtra(QUERY_ID, 0);

		ContentResolver resolver = this.getContentResolver();

		/* get the query name from the database */
		Cursor queryCursor = resolver.query(ContentUris.withAppendedId(
				BugzillaProvider.URI_GET_QUERY, mQueryId), null, null, null,
				null);
		queryCursor.moveToFirst();

		/* set caption to query name */
		TextView titleCaption = (TextView) findViewById(R.id.title_caption2);
		titleCaption.setText(queryCursor.getString(queryCursor
				.getColumnIndex(BugzillaDatabase.FIELD_NAME_NAME)));

		Cursor resultsCursor = resolver.query(ContentUris.withAppendedId(
				BugzillaProvider.URI_GET_ALL_BUGS_OF_QUERY, mQueryId), null,
				null, null, null);

		startManagingCursor(resultsCursor);
		
		ListAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.two_line_list_item, resultsCursor,
				new String[] { 
					BugzillaDatabase.FIELD_NAME_SUMMARY,
					BugzillaDatabase.FIELD_NAME_STATUS }, 
					new int[] {
						android.R.id.text1, 
						android.R.id.text2 });

		setListAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		/* get data if it exists */
		Log.d(TAG, "onResume");

		IntentFilter filter = new IntentFilter(
				BugzillaServiceHelper.TASK_RUN_QUERY);
		mRequestReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				if (intent.hasExtra(BugzillaServiceHelper.TASK_QUERY_ID)) {
					long resultQueryId = intent.getLongExtra(
							BugzillaServiceHelper.TASK_QUERY_ID, 0);

					Log.d(TAG, "Received intent " + intent.getAction()
							+ ", query ID " + resultQueryId);

					if (resultQueryId == mQueryId) {

						int resultCode = intent.getIntExtra(
								BugzillaServiceHelper.TASK_RESULT_CODE, 0);

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
		LocalBroadcastManager broadcastManager = LocalBroadcastManager
				.getInstance(app);
		broadcastManager.registerReceiver(mRequestReceiver, filter);

		runQuery(false);
	}

	@Override
	protected void onPause() {

		super.onPause();

		Log.d(TAG, "onPause");

		// Unregister for broadcast
		if (mRequestReceiver != null) {

			try {

				BugzillaApplication app = (BugzillaApplication) getApplication();
				LocalBroadcastManager broadcastManager = LocalBroadcastManager
						.getInstance(app);
				broadcastManager.unregisterReceiver(mRequestReceiver);

			} catch (IllegalArgumentException e) {

				Log.e(TAG, e.getLocalizedMessage(), e);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.results_options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_refresh_query:
			runQuery(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void runQuery(boolean updateResults) {

		BugzillaApplication app = (BugzillaApplication) getApplication();
		BugzillaServiceHelper helper = app.getBugzillaServiceHelper();

		if (!helper.isQueryRunning(mQueryId)) {

			helper.runQuery(mQueryId, updateResults);
		}

		setStatus(Status.QUERY_PENDING);
	}

	/**
	 * Sets the status of obtaining data from the service, and updates the UI
	 * 
	 * @param status
	 *            the new status
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
