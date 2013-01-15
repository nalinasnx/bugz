package com.webaltry.bugz;

import android.widget.ArrayAdapter;
import android.view.View;
import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.widget.TextView;


public class QueryAdapter extends ArrayAdapter<Query> {
    
	private ArrayList<Query> items;
    private Activity context;
    
    public QueryAdapter(Activity context, int textViewResourceId, ArrayList<Query> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.items = items;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
  
        View view = convertView;
        
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.query_row, null);
        }
        
        Query query = items.get(position);
        
        if (query != null) {
            
            // name
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(query.name);
             
            TextView stuff = (TextView) view.findViewById(R.id.stuff);
            stuff.setText("test");
             
        }
        
        return view;
        
    }    
}
