package com.webaltry.bugz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import android.os.Parcel;
import android.os.Parcelable;

public class QueryConstraint implements Parcelable {
    public String field;
    public String value;
    
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public void writeToParcel(Parcel out, int arg1) {
        out.writeString(field);
        out.writeString(value);
    }
    public QueryConstraint(String field, String value) {
        this.field = field;
        this.value = value;
    }
    
    private QueryConstraint(Parcel in) {
        field = in.readString();
        value = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public QueryConstraint createFromParcel(Parcel in) {
            return new QueryConstraint(in);
        }

        public QueryConstraint[] newArray(int size) {
            return new QueryConstraint[size];
        }
    };
    
    public String[] getValues() {
    	
    	if (value == null)
    		return null;
    	
    	if (value.isEmpty())
    		return null;
    	
    	//String[] values = new String[0];
    	ArrayList<String> valuesList = new ArrayList<String>();
    	StringTokenizer tokens = new StringTokenizer(value,",");
		
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			token = token.trim();
			if (!token.isEmpty())
				valuesList.add(token);
		}
		
		if (valuesList.isEmpty())
			return null;
		
		String[] values = new String[valuesList.size()];
		values = valuesList.toArray(values);
	    
    	return values;
    }

}
