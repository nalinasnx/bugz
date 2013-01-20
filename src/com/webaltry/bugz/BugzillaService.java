
package com.webaltry.bugz;


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class BugzillaService extends IntentService {

    private static final String TAG = BugzillaService.class.getSimpleName();

    public static final String SERVICE_CALLBACK = "com.webaltry.bugz.service.callback";
    public static final String SERVICE_TASK = "com.webaltry.bugz.service.task";
    public static final String SERVICE_QUERY = "com.webaltry.bugz.service.query";
    public static final String SERVICE_QUERY_ID = "com.webaltry.bugz.service.queryid";
    public static final String SERVICE_UPDATE_RESULTS = "com.webaltry.bugz.service.update_results";
    public static final String ORIGINAL_INTENT_EXTRA = "com.webaltry.bugz.service.original_intent_extra";

    public static final int SERVICE_TASK_CREATE_QUERY = 1;
    public static final int SERVICE_TASK_RUN_QUERY = 2;
    public static final int SERVICE_TASK_UPDATE_QUERY = 3;

    public BugzillaService() {
        super("BugzillaService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "onHandleIntent");
        
        BugzillaProcessor processor = new BugzillaProcessor();
        BugzillaApplication app = (BugzillaApplication) getApplication();

        try {

            if (!intent.hasExtra(SERVICE_TASK))
                throw new Exception("Service task type missing");
            
            int task = intent.getIntExtra(SERVICE_TASK, 0);

            switch (task) {

                case SERVICE_TASK_CREATE_QUERY: {
                    
                    Log.d(TAG, "SERVICE_TASK_CREATE_QUERY");
                    Query query = intent.getParcelableExtra(SERVICE_QUERY);
                    if (query != null) {
                        processor.createQuery(app, query, createCallback(intent));
                        return;
                    }

                }
                case SERVICE_TASK_RUN_QUERY: {
                    
                    if (!intent.hasExtra(SERVICE_QUERY_ID))
                        throw new Exception("Query id missing");
                    
                    long queryId = intent.getLongExtra(SERVICE_QUERY_ID, 0);
                    boolean updateResults = intent.getBooleanExtra(SERVICE_UPDATE_RESULTS, false);
                    
                    processor.runQuery(app, queryId, updateResults, createCallback(intent));
                    return;
                }
                case SERVICE_TASK_UPDATE_QUERY: {
                    
                    Log.d(TAG, "SERVICE_TASK_UPDATE_QUERY");
                    Query query = intent.getParcelableExtra(SERVICE_QUERY);
                    if (query != null) {
                        processor.updateQuery(app, query, createCallback(intent));
                        return;
                    }

                }
           }
        } catch (Exception e) {

            e.printStackTrace();
            onRequestFail(1, intent);
        }
    }

    private BugzillaProcessorCallback createCallback(final Intent intent) {

        BugzillaProcessorCallback callback = new BugzillaProcessorCallback() {

            @Override
            public void requestComplete(int resultCode) {

                ResultReceiver receiver = intent.getParcelableExtra(SERVICE_CALLBACK);
                Bundle resultData = new Bundle();
                resultData.putParcelable(ORIGINAL_INTENT_EXTRA, intent);
                receiver.send(0, resultData);
            }
        };
        return callback;
    }

    private void onRequestFail(int failCode, final Intent intent) {

        ResultReceiver receiver = intent.getParcelableExtra(SERVICE_CALLBACK);
        Bundle resultData = new Bundle();
        resultData.putParcelable(ORIGINAL_INTENT_EXTRA, intent);
        receiver.send(failCode, resultData);

    }

}
