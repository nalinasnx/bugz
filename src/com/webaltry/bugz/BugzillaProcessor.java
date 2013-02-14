package com.webaltry.bugz;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.j2bugzilla.base.Bug;

public class BugzillaProcessor {

	private static final String TAG = BugzillaProcessor.class.getSimpleName();

	public void createQuery(BugzillaApplication app, Query query,
			BugzillaProcessorCallback callback) {

		Log.d(TAG, "createQuery");

		if (query.idValid == true)
			return;

		/* insert record in "queries" table */
		ContentResolver resolver = app.getContentResolver();
		ContentValues values = new ContentValues();

		values.put(BugzillaDatabase.FIELD_NAME_NAME, query.name);
		values.put(BugzillaDatabase.FIELD_NAME_DESCRIPTION, query.description);

		for (QueryConstraint constraint : query.constraints) {
			values.put(constraint.databaseFieldName, constraint.value);
		}

		Uri uri = resolver.insert(BugzillaProvider.URI_ADD_QUERY, values);
		String id = uri.getLastPathSegment();
		query.id = Long.parseLong(id);
		query.idValid = true;

		callback.requestComplete(BugzillaService.SERVICE_RESULT_SUCCESS);
	}

	public void updateQuery(BugzillaApplication app, Query query,
			BugzillaProcessorCallback callback) {

		Log.d(TAG, "createQuery");

		if (query.idValid == false)
			return;

		/* insert record in "queries" table */
		ContentResolver resolver = app.getContentResolver();

		ContentValues values = new ContentValues();
		values.put(BugzillaDatabase.FIELD_NAME_ID, query.id);
		values.put(BugzillaDatabase.FIELD_NAME_NAME, query.name);
		values.put(BugzillaDatabase.FIELD_NAME_DESCRIPTION, query.description);
		for (QueryConstraint constraint : query.constraints) {
			values.put(constraint.databaseFieldName, constraint.value);
		}

		resolver.update(BugzillaProvider.URI_UPDATE_QUERY, values, null, null);

		callback.requestComplete(BugzillaService.SERVICE_RESULT_SUCCESS);
	}

	public void runQuery(BugzillaApplication app, long queryId,
			boolean updateResults, BugzillaProcessorCallback callback) {

		ContentResolver resolver = app.getContentResolver();

		if (updateResults) {

			/* delete any existing records in "results" table */
			resolver.delete(ContentUris.withAppendedId(
					BugzillaProvider.URI_DELETE_RESULTS, queryId), null, null);
		} else {

			// before running the query, check the database to see if the
			// results table contains any records for the input
			// query, if so, do nothing
			Cursor resultCursor = resolver.query(ContentUris.withAppendedId(
					BugzillaProvider.URI_GET_RESULTS_COUNT, queryId), null,
					null, null, null);
			resultCursor.moveToFirst();
			int count = resultCursor.getInt(0);

			if (count > 0) {
				callback.requestComplete(BugzillaService.SERVICE_RESULT_SUCCESS);
				return;
			}
		}

		/* get query definition from database */
		Cursor queryCursor = resolver.query(ContentUris.withAppendedId(
				BugzillaProvider.URI_GET_QUERY, queryId), null, null, null,
				null);
		queryCursor.moveToFirst();

		ArrayList<QueryConstraint> constraints = new ArrayList<QueryConstraint>();

		String value = queryCursor.getString(queryCursor
				.getColumnIndex(BugzillaDatabase.FIELD_NAME_ASSIGNEE));
		if (value != null && !value.isEmpty())
			constraints.add(new QueryConstraint(
					BugzillaDatabase.FIELD_NAME_ASSIGNEE, value));

		value = queryCursor.getString(queryCursor
				.getColumnIndex(BugzillaDatabase.FIELD_NAME_STATUS));
		if (value != null && !value.isEmpty())
			constraints.add(new QueryConstraint(
					BugzillaDatabase.FIELD_NAME_STATUS, value));

		Bugzilla bugzilla = app.getBugzilla();

		if (!bugzilla.isConnected()) {
			callback.requestComplete(BugzillaService.SERVICE_RESULT_FAIL);
			return;
		}

		/* delete any existing records in "results" table */
		resolver.delete(ContentUris.withAppendedId(
				BugzillaProvider.URI_DELETE_RESULTS, queryId), null, null);

		ArrayList<BugzillaBug> bugs = bugzilla.searchBugs(constraints);
		if (bugs == null) {
			callback.requestComplete(BugzillaService.SERVICE_RESULT_FAIL);
			return;
		}

		for (BugzillaBug bug : bugs) {

			/* create entry in "results" table */

			ContentValues values = new ContentValues();
			values.put(BugzillaDatabase.FIELD_NAME_QUERY_ID, queryId);
			values.put(BugzillaDatabase.FIELD_NAME_BUG_ID, bug.getID());

			resolver.insert(BugzillaProvider.URI_ADD_RESULT, values);

			values.clear();
			bug.getValues(values);

			resolver.insert(BugzillaProvider.URI_ADD_OR_REPLACE_BUG, values);
		}

		callback.requestComplete(BugzillaService.SERVICE_RESULT_SUCCESS);
	}

}
