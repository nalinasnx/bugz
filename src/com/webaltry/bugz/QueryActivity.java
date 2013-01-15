
package com.webaltry.bugz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class QueryActivity extends Activity {
    
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

        final TextView queryName = (TextView) findViewById(R.id.queryName);
        final TextView queryDescription = (TextView) findViewById(R.id.queryDescription);
        final TextView queryAssignee = (TextView) findViewById(R.id.queryAssignee);
        
        queryName.setText("My First Query");
        queryDescription.setText("This query rocks!");
        

        titleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Log.d(TAG, "Create Query");
                
                Query query = new Query();
                query.name = queryName.getText().toString();
                query.description = queryDescription.getText().toString();
                query.constraints.add(new QueryConstraint("assigned_to", queryAssignee.getText()
                        .toString()));

                BugzillaApplication app = (BugzillaApplication) getApplication();
                BugzillaServiceHelper helper = app.getBugzillaServiceHelper();
                helper.createQuery(query);

                finish();

            }
        });
    }

}
