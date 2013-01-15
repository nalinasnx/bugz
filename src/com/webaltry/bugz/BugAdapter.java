package com.webaltry.bugz;

import java.util.ArrayList;

import com.j2bugzilla.base.Bug;

import android.widget.ArrayAdapter;
import android.view.View;
import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;


public class BugAdapter extends ArrayAdapter<Bug> {
    
	private ArrayList<Bug> items;
    private Activity context;
    
    public BugAdapter(Activity context, int textViewResourceId, ArrayList<Bug> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.items = items;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
  
        View view = convertView;
        
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.bug_row, null);
        }
        
        Bug bug = items.get(position);
        
        if (bug != null) {
            
            // name
            TextView name = (TextView) view.findViewById(R.id.name);
            
            int bug_id = bug.getID();
            name.setText(Integer.toString(bug_id));
            
            //name.setText(bug.getID());
             
            TextView stuff = (TextView) view.findViewById(R.id.stuff);
            
            String bug_summary = bug.getSummary();
            if (bug_summary == null)
            	bug_summary = "no bug summary";
            stuff.setText(bug_summary);
            
             
        }
        
        return view;
        
    }    
}
