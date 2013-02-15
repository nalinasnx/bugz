
package com.webaltry.bugz;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class BugzillaDatabase extends SQLiteOpenHelper {

    private static final String TAG = BugzillaDatabase.class.getSimpleName();

    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "bugz";

    public static final String TABLE_NAME_QUERIES = "queries";
    public static final String TABLE_NAME_RESULTS = "results";
    public static final String TABLE_NAME_BUGS = "bugs";

    public static final String FIELD_NAME_ID = "_id";
    public static final String FIELD_NAME_NAME = "name";
    public static final String FIELD_NAME_DESCRIPTION = "description";
    public static final String FIELD_NAME_QUERY_ID = "queryId";
    public static final String FIELD_NAME_LAST_RUN = "last_run";
    
    // BUGZ: bug_id
    public static final String FIELD_NAME_BUG_ID = "BugId";	
    // BUGZ: component
    public static final String FIELD_NAME_COMPONENT = "Component";
    // BUGZ:
    public static final String FIELD_NAME_PRODUCT = "Product";
    // BUGZ: short_desc
    public static final String FIELD_NAME_SUMMARY = "Summary";
    // BUGZ: longdesc
    public static final String FIELD_NAME_COMMENT = "Comment";
    // BUGZ:
    public static final String FIELD_NAME_ASSIGNEE = "AssignedTo";
    // BUGZ:
    public static final String FIELD_NAME_CREATOR = "CreatedBy";
    // BUGZ:
    public static final String FIELD_NAME_CREATED = "Created";
    // BUGZ:
    public static final String FIELD_NAME_MODIFIED = "Changed";
    // BUGZ: bug_status
    public static final String FIELD_NAME_STATUS = "Status";
    // BUGZ:
    public static final String FIELD_NAME_PRIORITY = "Priority";
    // BUGZ: bug_severity
    public static final String FIELD_NAME_SEVERITY = "Severity";
    // BUGZ: resolution
    public static final String FIELD_NAME_RESOLUTION = "Resolution";

    private static final String CREATE_TABLE_QUERIES =
            "CREATE TABLE " + TABLE_NAME_QUERIES + " (" +
                    FIELD_NAME_ID + " INTEGER PRIMARY KEY, " +
            		
                    FIELD_NAME_NAME + " TEXT, " +
                    FIELD_NAME_DESCRIPTION + " TEXT, " +
                    
                    FIELD_NAME_ASSIGNEE + " TEXT, " +
                    FIELD_NAME_PRODUCT + " TEXT, " +
                    FIELD_NAME_COMPONENT + " TEXT, " +
                    FIELD_NAME_STATUS + " TEXT, " +
                    FIELD_NAME_PRIORITY + " TEXT, " +
                    FIELD_NAME_SEVERITY + " TEXT, " +
                    FIELD_NAME_RESOLUTION + " TEXT, " +
                    FIELD_NAME_CREATOR + " TEXT, " +
                    
                    FIELD_NAME_LAST_RUN + " TEXT);";

    private static final String CREATE_TABLE_RESULTS =
            "CREATE TABLE " + TABLE_NAME_RESULTS + " (" +
                    FIELD_NAME_ID + " INTEGER PRIMARY KEY, " +
                    FIELD_NAME_QUERY_ID + " INTEGER, " +
                    FIELD_NAME_BUG_ID + " INTEGER);";

    private static final String CREATE_TABLE_BUGS =
            "CREATE TABLE " + TABLE_NAME_BUGS + " (" +
                    FIELD_NAME_ID + " INTEGER PRIMARY KEY, " +
                    FIELD_NAME_BUG_ID + " INTEGER NOT NULL UNIQUE, " +
                    
                    FIELD_NAME_ASSIGNEE + " TEXT, " +
                    FIELD_NAME_PRODUCT + " TEXT, " +
                    FIELD_NAME_COMPONENT + " TEXT, " +
                    FIELD_NAME_STATUS + " TEXT, " +
                    FIELD_NAME_PRIORITY + " TEXT, " +
                    FIELD_NAME_SEVERITY + " TEXT, " +
                    FIELD_NAME_RESOLUTION + " TEXT, " +
                    FIELD_NAME_CREATOR + " TEXT, " +

                    FIELD_NAME_SUMMARY + " TEXT, " +
                    FIELD_NAME_CREATED + " TEXT, " +
                    FIELD_NAME_MODIFIED + " TEXT);";

    public static String[] queryFields = new String[] {
    	FIELD_NAME_ASSIGNEE, 
    	FIELD_NAME_PRODUCT, 
    	FIELD_NAME_COMPONENT,
    	FIELD_NAME_STATUS,
    	FIELD_NAME_PRIORITY,
    	FIELD_NAME_SEVERITY,
    	FIELD_NAME_RESOLUTION,
    	FIELD_NAME_CREATOR,
    	};
    
//    static List<String> getQueryFields() {
//    	
//    	List<String> strings = new ArrayList<String>();
//    	
//    }
//    
    BugzillaDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_QUERIES);
        db.execSQL(CREATE_TABLE_RESULTS);
        db.execSQL(CREATE_TABLE_BUGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_QUERIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RESULTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BUGS);
        onCreate(db);
    }

    public long insertQuery(ContentValues values) {
        Log.d(TAG, "insertQuery");
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_NAME_QUERIES, null, values);
    }

    /**
     * Adds a single record to the results table, which contains a one-to-many
     * mapping of a query-id to bug-ids
     * 
     * @param values
     * @return
     */
    public long insertResult(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_NAME_RESULTS, null, values);
    }

    public long insertOrReplaceBug(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        return db.replace(TABLE_NAME_BUGS, null, values);
    }

    public Cursor queryQuery(String queryId, String[] projection, String selection, String sortOrder) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(TABLE_NAME_QUERIES);
        qb.appendWhere(FIELD_NAME_ID + " = " + queryId);

        Cursor c = qb.query(db, projection, selection, null, null, null, sortOrder);
        return c;
    }

    public Cursor queryBug(String bugId, String[] projection, String selection, String sortOrder) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(TABLE_NAME_BUGS);
        qb.appendWhere(FIELD_NAME_BUG_ID + " = " + bugId);

        Cursor c = qb.query(db, projection, selection, null, null, null, sortOrder);
        return c;
    }

    public Cursor queryQueries(String[] projection, String selection, String sortOrder) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(TABLE_NAME_QUERIES);

        Cursor c = qb.query(db, projection, selection, null, null, null, sortOrder);
        return c;
    }

    public Cursor queryResults(String[] projection, String selection, String sortOrder) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(TABLE_NAME_RESULTS);

        Cursor c = qb.query(db, projection, selection, null, null, null, sortOrder);
        return c;
    }

    public Cursor queryBugs(String queryId, String[] projection, String selection, String sortOrder) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(TABLE_NAME_RESULTS +
                " LEFT OUTER JOIN " + TABLE_NAME_BUGS + " ON " +
                TABLE_NAME_RESULTS + "." + FIELD_NAME_BUG_ID + " = " + TABLE_NAME_BUGS + "."
                + FIELD_NAME_BUG_ID);

        qb.appendWhere(TABLE_NAME_RESULTS + "." + FIELD_NAME_QUERY_ID + " = " + queryId);

        Cursor c = qb.query(db, projection, selection, null, null, null, sortOrder);
        return c;
    }

    public Cursor resultCount(String queryId, String[] projection, String selection,
            String sortOrder) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME_RESULTS);

        HashMap<String, String> countMap = new HashMap<String, String>();
        countMap.put("count", "count(*)");
        qb.setProjectionMap(countMap);

        qb.appendWhere(FIELD_NAME_QUERY_ID + " = " + queryId);

        Cursor c = qb.query(db, projection, selection, null, null, null, sortOrder);
        return c;
    }

    public int deleteResults(String queryId) {
        SQLiteDatabase db = getReadableDatabase();

        return db.delete(TABLE_NAME_RESULTS, FIELD_NAME_QUERY_ID + "=" + queryId, null);
    }

    public long updateQuery(ContentValues values) {
        
        SQLiteDatabase db = getWritableDatabase();
        return db.replace(TABLE_NAME_QUERIES, null, values);
     }
}
