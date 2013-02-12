package com.webaltry.bugz;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;

public class BugzillaBug {
	
	private Map<String, Object> mData = new HashMap<String, Object>();
	
	public BugzillaBug(Map<String, Object> bugMap) {
		
		Object value = bugMap.get("assigned_to");
		if (value != null)
			mData.put(BugzillaDatabase.FIELD_NAME_ASSIGNEE, value); // string
		
		value = bugMap.get("creator");
		if (value != null)
			mData.put(BugzillaDatabase.FIELD_NAME_CREATOR, value); // string
		
		value = bugMap.get("component");
		if (value != null)
			mData.put(BugzillaDatabase.FIELD_NAME_COMPONENT, value); // string
		
		value = bugMap.get("id");
		if (value != null)
			mData.put(BugzillaDatabase.FIELD_NAME_BUG_ID, value); // int
		
		value = bugMap.get("priority");
		if (value != null)
			mData.put(BugzillaDatabase.FIELD_NAME_PRIORITY, value); // string
		
		value = bugMap.get("product");
		if (value != null)
			mData.put(BugzillaDatabase.FIELD_NAME_PRODUCT, value); // string
		
		value = bugMap.get("resolution");
		if (value != null)
			mData.put(BugzillaDatabase.FIELD_NAME_RESOLUTION, value); // string
		
		value = bugMap.get("severity");
		if (value != null)
			mData.put(BugzillaDatabase.FIELD_NAME_SEVERITY, value); // string
		
		value = bugMap.get("status");
		if (value != null)
			mData.put(BugzillaDatabase.FIELD_NAME_STATUS, value); // string
		
		value = bugMap.get("summary");
		if (value != null)
			mData.put(BugzillaDatabase.FIELD_NAME_SUMMARY, value); // string
		
	}
	
	public int getID() {
		return (Integer)mData.get(BugzillaDatabase.FIELD_NAME_BUG_ID);
	}
	
	public void getValues(ContentValues values) {
		
		for (Map.Entry<String, Object> entry : mData.entrySet())
		{
			Object value = entry.getValue();
			
			if (value instanceof String)
				values.put(entry.getKey(), (String)value);
			
			else if (value instanceof Integer)
				values.put(entry.getKey(), (Integer)value);
		}	
		
	}

	
	
}
