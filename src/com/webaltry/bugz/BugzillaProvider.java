
package com.webaltry.bugz;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class BugzillaProvider extends ContentProvider {

    public static final String AUTHORITY = "com.webaltry.bugz.bugzillaprovider";
    static final String SCHEME = "content://";
    public static final String URI_PREFIX = SCHEME + AUTHORITY;

    private static final String ADD_QUERY = "add_query";
    public static final Uri URI_ADD_QUERY = Uri.parse(URI_PREFIX + "/" + ADD_QUERY);

    private static final String GET_QUERY = "get_query";
    public static final Uri URI_GET_QUERY = Uri.parse(URI_PREFIX + "/" + GET_QUERY);
    
    private static final String UPDATE_QUERY = "update_query";
   public static final Uri URI_UPDATE_QUERY = Uri.parse(URI_PREFIX + "/" + UPDATE_QUERY);

    private static final String GET_ALL_QUERIES = "get_all_queries";
    public static final Uri URI_GET_ALL_QUERIES = Uri.parse(URI_PREFIX + "/" + GET_ALL_QUERIES);

    private static final String GET_RESULTS_COUNT = "get_results_count";
    public static final Uri URI_GET_RESULTS_COUNT = Uri.parse(URI_PREFIX + "/" + GET_RESULTS_COUNT);

    /* add an entry to the results table */
    private static final String ADD_RESULT = "add_result";
    public static final Uri URI_ADD_RESULT = Uri.parse(URI_PREFIX + "/" + ADD_RESULT);

    private static final String GET_RESULT = "get_result";
    public static final Uri URI_GET_RESULT = Uri.parse(URI_PREFIX + "/" + GET_RESULT);

    private static final String ADD_OR_REPLACE_BUG = "add_or_replace_bug";
    public static final Uri URI_ADD_OR_REPLACE_BUG = Uri.parse(URI_PREFIX + "/"
            + ADD_OR_REPLACE_BUG);

    private static final String GET_BUG = "get_bug";
    public static final Uri URI_GET_BUG = Uri.parse(URI_PREFIX + "/" + GET_BUG);

    private static final String DELETE_RESULTS = "delete_results";
    public static final Uri URI_DELETE_RESULTS = Uri.parse(URI_PREFIX + "/" + DELETE_RESULTS);

    private static final String GET_ALL_BUGS_OF_QUERY = "get_all_bugs_of_query";
    public static final Uri URI_GET_ALL_BUGS_OF_QUERY = Uri.parse(URI_PREFIX + "/"
            + GET_ALL_BUGS_OF_QUERY);

    private static final int CODE_ADD_QUERY = 100;
    private static final int CODE_GET_QUERY = 101;
    private static final int CODE_GET_ALL_QUERIES = 102;
    private static final int CODE_GET_RESULTS_COUNT = 103;
    private static final int CODE_ADD_RESULT = 104;
    private static final int CODE_GET_RESULT = 105;
    private static final int CODE_ADD_OR_REPLACE_BUG = 106;
    private static final int CODE_GET_BUG = 107;
    private static final int CODE_DELETE_RESULTS = 108;
    private static final int CODE_GET_ALL_BUGS_OF_QUERY = 109;
    private static final int CODE_UPDATE_QUERY = 110;

    private static final UriMatcher uriMatcher;
    static {

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, ADD_QUERY, CODE_ADD_QUERY);
        uriMatcher.addURI(AUTHORITY, GET_QUERY + "/#", CODE_GET_QUERY);
        uriMatcher.addURI(AUTHORITY, ADD_RESULT, CODE_ADD_RESULT);
        uriMatcher.addURI(AUTHORITY, GET_RESULT + "/#", CODE_GET_RESULT);
        uriMatcher.addURI(AUTHORITY, ADD_OR_REPLACE_BUG, CODE_ADD_OR_REPLACE_BUG);
        uriMatcher.addURI(AUTHORITY, GET_BUG + "/#", CODE_GET_BUG);
        uriMatcher.addURI(AUTHORITY, GET_RESULTS_COUNT + "/#", CODE_GET_RESULTS_COUNT);
        uriMatcher.addURI(AUTHORITY, DELETE_RESULTS + "/#", CODE_DELETE_RESULTS);
        uriMatcher.addURI(AUTHORITY, GET_ALL_QUERIES, CODE_GET_ALL_QUERIES);
        uriMatcher.addURI(AUTHORITY, GET_ALL_BUGS_OF_QUERY + "/#", CODE_GET_ALL_BUGS_OF_QUERY);
        uriMatcher.addURI(AUTHORITY, UPDATE_QUERY, CODE_UPDATE_QUERY);
   }

    private BugzillaDatabase mDatabase;

    @Override
    public boolean onCreate() {

        mDatabase = new BugzillaDatabase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int delete(Uri uri, String arg1, String[] arg2) {

        switch (uriMatcher.match(uri)) {

            case CODE_DELETE_RESULTS: {
                String queryId = uri.getLastPathSegment();
                return mDatabase.deleteResults(queryId);
            }
        }

        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        switch (uriMatcher.match(uri)) {

            case CODE_ADD_QUERY: {
                /* add a record to the queries table */
                long queryId = mDatabase.insertQuery(values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(URI_GET_QUERY + "/" + queryId);
            }

            case CODE_ADD_RESULT: {
                /* insert a record in the results table */
                long recordId = mDatabase.insertResult(values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(URI_GET_RESULT + "/" + recordId);
            }

            case CODE_ADD_OR_REPLACE_BUG: {
                /* insert a record in the bugs table */
                long recordId = mDatabase.insertOrReplaceBug(values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(URI_GET_BUG + "/" + recordId);
            }
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {

        switch (uriMatcher.match(uri)) {

            case CODE_GET_RESULTS_COUNT: {
                /* query results table for count for specified query id */
                String queryId = uri.getLastPathSegment();
                Cursor cursor = mDatabase.resultCount(queryId, projection, selection, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }

            case CODE_GET_QUERY: {
                /* query the queries table for a specific record */
                String queryId = uri.getLastPathSegment();
                Cursor cursor = mDatabase.queryQuery(queryId, projection, selection, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }

            case CODE_GET_ALL_QUERIES: {
                /* query the queries table */
                Cursor cursor = mDatabase.queryQueries(projection, selection, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }

            case CODE_GET_ALL_BUGS_OF_QUERY: {
                String queryId = uri.getLastPathSegment();
                Cursor cursor = mDatabase.queryBugs(queryId, projection, selection, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        switch (uriMatcher.match(uri)) {

            case CODE_UPDATE_QUERY: {
                /* query the queries table for a specific record */
               //long queryId = Long.valueOf(uri.getLastPathSegment());
               // Cursor cursor = mDatabase.updateQuery(queryId, values);
               // cursor.setNotificationUri(getContext().getContentResolver(), uri);
              //  return cursor;
               mDatabase.updateQuery(values);
                //if (updatedId == 
                //    throw new IllegalArgumentException("??");
                return 1;
            }
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

}
