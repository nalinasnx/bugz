package com.webaltry.bugz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

public class QueryActivity extends Activity {

	public static final String QUERY_ID = "QUERY_ID";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.query);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		TextView titleCaption = (TextView) findViewById(R.id.title_caption);
		titleCaption.setText("New Query");
		Button titleButton = (Button) findViewById(R.id.title_button);
		titleButton.setText("Save");

		BugzillaApplication app = (BugzillaApplication) getApplication();
		final Bugzilla bugz = app.getBugzilla();

		final TextView queryName = (TextView) findViewById(R.id.queryName);
		final TextView queryDescription = (TextView) findViewById(R.id.queryDescription);
		final TextView queryAssignee = (TextView) findViewById(R.id.queryAssignee);

		
		
		
		final MultiAutoCompleteTextView queryProduct = (MultiAutoCompleteTextView) findViewById(R.id.queryProduct);
		queryProduct
				.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		
		QueryFieldButton buttonProduct = (QueryFieldButton)findViewById(R.id.queryProductButton);
		buttonProduct.configure(BugzillaDatabase.FIELD_NAME_PRODUCT, "Select Product", queryProduct);
		
		
		
		final MultiAutoCompleteTextView queryStatus = (MultiAutoCompleteTextView) findViewById(R.id.queryStatus);
		queryStatus
				.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		
		QueryFieldButton buttonStatus = (QueryFieldButton)findViewById(R.id.queryStatusButton);
		buttonStatus.configure(BugzillaDatabase.FIELD_NAME_STATUS, "Select Status", queryStatus);

		
		
		
		updateAutoCompleteValues();

		/* look for query id; if present, editing existing query */
		final Intent intent = getIntent();
		final boolean editingQuery = intent.hasExtra(QUERY_ID);

		if (editingQuery) {

			long queryId = getIntent().getLongExtra(QUERY_ID, 0);

			ContentResolver resolver = this.getContentResolver();

			/* get the query name from the database */
			Cursor queryCursor = resolver.query(ContentUris.withAppendedId(
					BugzillaProvider.URI_GET_QUERY, queryId), null, null, null,
					null);
			queryCursor.moveToFirst();

			queryName.setText(queryCursor.getString(queryCursor
					.getColumnIndex(BugzillaDatabase.FIELD_NAME_NAME)));

			queryDescription.setText(queryCursor.getString(queryCursor
					.getColumnIndex(BugzillaDatabase.FIELD_NAME_DESCRIPTION)));

			queryAssignee.setText(queryCursor.getString(queryCursor
					.getColumnIndex(BugzillaDatabase.FIELD_NAME_ASSIGNEE)));

			queryStatus.setText(queryCursor.getString(queryCursor
					.getColumnIndex(BugzillaDatabase.FIELD_NAME_STATUS)));

		} else {

			queryName.setText("My First Query");
			queryDescription.setText("This query rocks!");
			queryAssignee.setText("");
		}

		/*
		 * make sure connected to server; need the legal values for certain
		 * fields for the UI
		 */

		if (!bugz.isConnected()) {
			/* run the login activity */
			startActivityForResult(new Intent(this, LoginActivity.class), 1);
		}

//		final Button queryStatusButton = (Button) findViewById(R.id.queryStatusButton);
//		queryStatusButton.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//
//				/* get the current text */
//				Editable items = queryStatus.getText();
//
//				/* get current items */
//				StringTokenizer tokens = new StringTokenizer(items.toString(),
//						",");
//				Set<String> currentValues = new HashSet<String>();
//				while (tokens.hasMoreTokens()) {
//					String token = tokens.nextToken();
//					token = token.trim();
//					if (!token.isEmpty())
//						currentValues.add(token);
//				}
//				
//				/* get available items */
//				ArrayList<String> statusValues = bugz
//						.getValues(BugzillaDatabase.FIELD_NAME_STATUS);
//				
//				
//				int size = statusValues.size();
//				boolean[] selected = new boolean[size];
//				int index = 0;
//				
//				List<CharSequence> availableValues = new ArrayList<CharSequence>();
//				for (String value : statusValues) {
//					availableValues.add(value);
//					
//					selected[index] = currentValues.contains(value);
//					index++;
//				}
//
//				AlertDialog.Builder builder = new AlertDialog.Builder(
//						QueryActivity.this);
//
//				builder.setTitle("Select Status")
//						// Specify the list array, the items to be selected by
//						// default (null for none),
//						// and the listener through which to receive callbacks
//						// when items are selected
//						.setMultiChoiceItems(
//								availableValues
//										.toArray(new CharSequence[size]),
//										selected,
//								new DialogInterface.OnMultiChoiceClickListener() {
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which, boolean isChecked) {
//										// if (isChecked) {
//										// // If the user checked the item, add
//										// it to the selected items
//										// mSelectedItems.add(which);
//										// } else if
//										// (mSelectedItems.contains(which)) {
//										// // Else, if the item is already in
//										// the array, remove it
//										// mSelectedItems.remove(Integer.valueOf(which));
//										// }
//									}
//								})
//
//						// Set the action buttons
//						.setPositiveButton("Ok",
//								new DialogInterface.OnClickListener() {
//									@Override
//									public void onClick(DialogInterface dialog,
//											int id) {
//										// User clicked OK, so save the
//										// mSelectedItems results somewhere
//										// or return them to the component that
//										// opened the dialog
//										// ...
//									}
//								})
//
//						.setNegativeButton("Cancel",
//								new DialogInterface.OnClickListener() {
//									@Override
//									public void onClick(DialogInterface dialog,
//											int id) {
//									}
//								});
//
//				AlertDialog alert = builder.create();
//				alert.show();
//
//			}
//
//		});
//
//		// return builder.create();

		titleButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// Log.d(TAG, "Create Query");

				Query query = new Query();
				query.name = queryName.getText().toString();
				query.description = queryDescription.getText().toString();

				String value = queryAssignee.getText().toString();
				if (!value.isEmpty())
					query.constraints.add(new QueryConstraint(
							BugzillaDatabase.FIELD_NAME_ASSIGNEE, value));

				value = queryStatus.getText().toString();
				if (queryStatus.length() > 0)
					query.constraints.add(new QueryConstraint(
							BugzillaDatabase.FIELD_NAME_STATUS, value));

				BugzillaApplication app = (BugzillaApplication) getApplication();
				BugzillaServiceHelper helper = app.getBugzillaServiceHelper();

				if (editingQuery) {
					query.id = getIntent().getLongExtra(QUERY_ID, 0);
					query.idValid = true;
					helper.updateQuery(query);
				} else {
					helper.createQuery(query);
				}
				finish();

			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		BugzillaApplication app = (BugzillaApplication) getApplication();
		Bugzilla bugz = app.getBugzilla();

		if (!bugz.isConnected()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle("Bugzilla");

			builder.setMessage(
					"Must connect to Bugzilla server to add or edit queries")
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									finish();
								}
							});

			AlertDialog alert = builder.create();

			alert.show();
		} else {
			/* after connecting, update auto-complete fields */
			updateAutoCompleteValues();
		}

	}

	private void updateAutoCompleteValues() {

		BugzillaApplication app = (BugzillaApplication) getApplication();
		Bugzilla bugz = app.getBugzilla();

		final MultiAutoCompleteTextView queryStatus = (MultiAutoCompleteTextView) findViewById(R.id.queryStatus);

		ArrayList<String> statusValues = bugz
				.getValues(BugzillaDatabase.FIELD_NAME_STATUS);
		if (statusValues != null)
			queryStatus.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, statusValues));

	}
}
