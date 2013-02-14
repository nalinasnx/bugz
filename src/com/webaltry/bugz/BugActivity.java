package com.webaltry.bugz;

import java.util.List;
import java.util.Vector;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BugActivity extends ListActivity {

	public static final String BUG_ID = "BUG_ID";
	private long mBugId = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.bug);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title2);

		/* get the bug id from the intent */
		mBugId = getIntent().getLongExtra(BUG_ID, 0);

		ContentResolver resolver = this.getContentResolver();

		Cursor bugCursor = resolver.query(ContentUris.withAppendedId(
				BugzillaProvider.URI_GET_BUG, mBugId), null, null, null, null);

		bugCursor.moveToFirst();

		Vector<RowData> data = new Vector<RowData>();
		data.add(new RowData(BugzillaDatabase.FIELD_NAME_BUG_ID, bugCursor
				.getLong(bugCursor
						.getColumnIndex(BugzillaDatabase.FIELD_NAME_BUG_ID))));
		data.add(new RowData(BugzillaDatabase.FIELD_NAME_COMPONENT, bugCursor
				.getString(bugCursor
						.getColumnIndex(BugzillaDatabase.FIELD_NAME_COMPONENT))));
		data.add(new RowData(BugzillaDatabase.FIELD_NAME_PRODUCT, bugCursor
				.getString(bugCursor
						.getColumnIndex(BugzillaDatabase.FIELD_NAME_PRODUCT))));
		data.add(new RowData(BugzillaDatabase.FIELD_NAME_SUMMARY, bugCursor
				.getString(bugCursor
						.getColumnIndex(BugzillaDatabase.FIELD_NAME_SUMMARY))));
		data.add(new RowData(BugzillaDatabase.FIELD_NAME_ASSIGNEE, bugCursor
				.getString(bugCursor
						.getColumnIndex(BugzillaDatabase.FIELD_NAME_ASSIGNEE))));
		data.add(new RowData(BugzillaDatabase.FIELD_NAME_CREATOR, bugCursor
				.getString(bugCursor
						.getColumnIndex(BugzillaDatabase.FIELD_NAME_CREATOR))));
		data.add(new RowData(BugzillaDatabase.FIELD_NAME_STATUS, bugCursor
				.getString(bugCursor
						.getColumnIndex(BugzillaDatabase.FIELD_NAME_STATUS))));
		data.add(new RowData(BugzillaDatabase.FIELD_NAME_PRIORITY, bugCursor
				.getString(bugCursor
						.getColumnIndex(BugzillaDatabase.FIELD_NAME_PRIORITY))));
		data.add(new RowData(BugzillaDatabase.FIELD_NAME_SEVERITY, bugCursor
				.getString(bugCursor
						.getColumnIndex(BugzillaDatabase.FIELD_NAME_SEVERITY))));
		data.add(new RowData(
				BugzillaDatabase.FIELD_NAME_RESOLUTION,
				bugCursor.getString(bugCursor
						.getColumnIndex(BugzillaDatabase.FIELD_NAME_RESOLUTION))));

		CustomAdapter adapter = new CustomAdapter(this, R.layout.bug_row, data);
		setListAdapter(adapter);
	}

	private class RowData {

		public String field;
		public String value;

		RowData(String field, String value) {
			this.field = field;
			this.value = value;
		}

		RowData(String field, long value) {
			this.field = field;
			this.value = Long.toString(value);
		}
	}

	private class CustomAdapter extends ArrayAdapter<RowData> {

		private Context mContext;

		public CustomAdapter(Context context, int textViewResourceId,
				List<RowData> objects) {
			super(context, textViewResourceId, objects);

			mContext = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.bug_row, null);
			}

			RowData rowData = getItem(position);

			TextView field = (TextView) convertView.findViewById(R.id.field);
			field.setText(rowData.field);

			TextView value = (TextView) convertView.findViewById(R.id.value);
			value.setText(rowData.value);

			return convertView;
		}
	}

}
