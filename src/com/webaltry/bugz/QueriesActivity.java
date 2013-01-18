package com.webaltry.bugz;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class QueriesActivity extends ListActivity {
	
     ListAdapter mAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        /* configure content with custom title bar */
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.queries);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title); 
        
        TextView titleCaption = (TextView)findViewById(R.id.title_caption);
        titleCaption.setText("Queries");
        final Button titleButton = (Button)findViewById(R.id.title_button);
        titleButton.setText("New Query");
        titleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(QueriesActivity.this, QueryActivity.class);
                startActivity(intent);
            }
        });
         
        /* query for all queries */ 
        Cursor cursor = this.getContentResolver().query(BugzillaProvider.URI_GET_ALL_QUERIES, null, null, null, null);
        startManagingCursor(cursor);
        mAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.two_line_list_item,
                cursor,
                new String[] {BugzillaDatabase.FIELD_NAME_NAME, BugzillaDatabase.FIELD_NAME_DESCRIPTION},
                new int[] {android.R.id.text1, android.R.id.text2});
        
        setListAdapter(mAdapter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.queries_options, menu);
        return true;
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        
        super.onListItemClick(l, v, position, id);
        
        Cursor c = ((SimpleCursorAdapter)l.getAdapter()).getCursor();
        c.moveToPosition(position);
        long queryId = c.getLong(0);
        
        //Query query = (Query)mAdapter.getItem(position);
        Intent intent = new Intent(QueriesActivity.this, QueryResultsActivity.class);
        intent.putExtra(QueryResultsActivity.QUERY_ID, queryId);
        startActivity(intent);
                
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    	switch (item.getItemId()) {

    	case R.id.menu_add_query:
    		//startActivity(new Intent(this, About.class));
    		return true;
    	case R.id.menu_connect:
    		startActivity(new Intent(this, LoginActivity.class));
    		return true;
      	case R.id.menu_settings:
    		//startActivity(new Intent(this, Help.class));
    		return true;
      	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
}
