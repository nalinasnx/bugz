
package com.webaltry.bugz;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class BugzillaServiceHelper {

    private static final String TAG = BugzillaServiceHelper.class.getSimpleName();

    public static String TASK_RUN_QUERY = "TASK_RUN_QUERY";
    public static String TASK_CREATE_QUERY = "TASK_CREATE_QUERY";

    public static String TASK_QUERY_ID = "TASK_QUERY_ID";
    public static String TASK_RESULT_CODE = "TASK_RESULT_CODE";

    private Context appContext;
    private Set<Long> mRunningQueries = new HashSet<Long>();

    public BugzillaServiceHelper(Context appContext) {
        this.appContext = appContext;
    }

    public void createQuery(Query query) {

         if (query.getIdValid())
            throw new IllegalArgumentException("Query already exists");

        /* create receiver of results from service */
        ResultReceiver serviceCallback = new ResultReceiver(null) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {

                handleReponseCreateQuery(resultCode, resultData);
            }
        };

        /* prepare data for service request */
        Intent intent = new Intent(appContext, BugzillaService.class);
        intent.putExtra(BugzillaService.SERVICE_TASK, BugzillaService.SERVICE_TASK_CREATE_QUERY);
        intent.putExtra(BugzillaService.SERVICE_QUERY, query);
        intent.putExtra(BugzillaService.SERVICE_CALLBACK, serviceCallback);

        /* request service */
        appContext.startService(intent);
    }

    public void updateQuery(Query query) {

         if (!query.getIdValid())
            throw new IllegalArgumentException("Query already exists");

        /* create receiver of results from service */
        ResultReceiver serviceCallback = new ResultReceiver(null) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {

                handleReponseUpdateQuery(resultCode, resultData);
            }
        };

        /* prepare data for service request */
        Intent intent = new Intent(appContext, BugzillaService.class);
        intent.putExtra(BugzillaService.SERVICE_TASK, BugzillaService.SERVICE_TASK_UPDATE_QUERY);
        intent.putExtra(BugzillaService.SERVICE_QUERY, query);
        intent.putExtra(BugzillaService.SERVICE_CALLBACK, serviceCallback);

        /* request service */
        appContext.startService(intent);
    }

    public void runQuery(long queryId, boolean updateResults) {

        Log.d(TAG, "runQuery");

        if (mRunningQueries.contains(queryId))
            return;

         mRunningQueries.add(queryId);

        /* create receiver of results from service */
        ResultReceiver serviceCallback = new ResultReceiver(null) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {

                handleReponseRunQuery(resultCode, resultData);
            }
        };

        /* prepare data for service request */
        Intent intent = new Intent(appContext, BugzillaService.class);
        intent.putExtra(BugzillaService.SERVICE_TASK, BugzillaService.SERVICE_TASK_RUN_QUERY);
        intent.putExtra(BugzillaService.SERVICE_QUERY_ID, queryId);
        intent.putExtra(BugzillaService.SERVICE_UPDATE_RESULTS, updateResults);
        intent.putExtra(BugzillaService.SERVICE_CALLBACK, serviceCallback);

        /* request service */
        appContext.startService(intent);
    }

    public boolean isQueryRunning(long queryId) {
        return this.mRunningQueries.contains(queryId);
    }

    private void handleReponseRunQuery(int resultCode, Bundle resultData) {

        Log.d(TAG, "handleReponseRunQuery");

        Intent originalIntent = (Intent) resultData
                .getParcelable(BugzillaService.ORIGINAL_INTENT_EXTRA);

        if (originalIntent != null && originalIntent.hasExtra(BugzillaService.SERVICE_QUERY_ID)) {

            long queryId = originalIntent.getLongExtra(BugzillaService.SERVICE_QUERY_ID, 0);
            mRunningQueries.remove(queryId);

            Intent result = new Intent(TASK_RUN_QUERY);
            result.putExtra(TASK_QUERY_ID, queryId);
            result.putExtra(TASK_RESULT_CODE, resultCode);

            /* broadcast results to activity, if running */
            LocalBroadcastManager broadcastManager = LocalBroadcastManager
                    .getInstance(appContext);

            if (broadcastManager != null) {
                broadcastManager.sendBroadcast(result);
            }
        }

    }

    private void handleReponseCreateQuery(int resultCode, Bundle resultData) {

        Log.d(TAG, "handleReponseCreateQuery");

        Intent originalIntent = (Intent) resultData
                .getParcelable(BugzillaService.ORIGINAL_INTENT_EXTRA);

        if (originalIntent != null) {

            Intent result = new Intent(TASK_CREATE_QUERY);

            /* broadcast results to activity, if running */
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(appContext);
            if (broadcastManager != null) {
                broadcastManager.sendBroadcast(result);
            }
        }
    }

    private void handleReponseUpdateQuery(int resultCode, Bundle resultData) {

        Log.d(TAG, "handleReponseUpdateQuery");

//        Intent originalIntent = (Intent) resultData
//                .getParcelable(BugzillaService.ORIGINAL_INTENT_EXTRA);
//
//        if (originalIntent != null) {
//
//            Intent result = new Intent(TASK_CREATE_QUERY);
//
//            /* broadcast results to activity, if running */
//            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(appContext);
//            if (broadcastManager != null) {
//                broadcastManager.sendBroadcast(result);
//            }
//        }
    }
}
