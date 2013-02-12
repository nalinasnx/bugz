package com.webaltry.bugz;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugzillaMethod;
import com.j2bugzilla.base.Product;

public class BugzillaFields implements BugzillaMethod {
	
	public static final String BUGZ_FIELD_BUG_ID = "bug_id";	
	public static final String BUGZ_FIELD_COMPONENT = "component";	
	public static final String BUGZ_FIELD_PRODUCT = "product";	
	public static final String BUGZ_FIELD_SHORT_DESCRIPTION = "short_desc";	
	public static final String BUGZ_FIELD_LONG_DESCRIPTION = "longdesc";	
	public static final String BUGZ_FIELD_ASSIGNED_TO = "assigned_to";	
	public static final String BUGZ_FIELD_CREATOR = "reporter";	
	public static final String BUGZ_FIELD_CREATED = "creation_ts";	
	public static final String BUGZ_FIELD_MODIFIED = "delta_ts";	
	public static final String BUGZ_FIELD_STATUS = "bug_status";	
	public static final String BUGZ_FIELD_PRIORITY = "priority";	
	public static final String BUGZ_FIELD_SEVERITY = "bug_severity";	
	public static final String BUGZ_FIELD_RESOLUTION = "resolution";	
	
	
	private final Map<String, BugzillaField> mDatabaseToBugz = new HashMap<String, BugzillaField>();
	//private final Map<String, String> mBugzToDatabase = new HashMap<String, String>();

	/**
	 * The {@code GetLegalValues} class allows clients to query their
	 * installation for information on the allowed values for fields in bug
	 * reports, which may be edited to be installation-specific. For example,
	 * the default workflow has changed from Bugzilla 3.x to 4.x. Additionally,
	 * custom priority and severity values may be defined.
	 * 
	 * @author Tom
	 * 
	 */

	private static final String METHOD_NAME = "Bug.fields";

	private final Map<Object, Object> params = new HashMap<Object, Object>();

	private Product product;

	/**
	 * Creates a new {@link GetLegalValues} instance on the specified
	 * {@link Fields field}.
	 * 
	 * @param field
	 *            A {@link Fields} enum value describing which field's values
	 *            should be retrieved.
	 */
	public BugzillaFields() {
	}

	public Map<String, BugzillaField> getDatabaseToBugz() {
		return mDatabaseToBugz;
	}

	//public Map<String, String> getBugzToDatabase() {
	//	return mBugzToDatabase;
	//}

	@Override
	public void setResultMap(Map<Object, Object> hash) {
		
		Object[] array = (Object[]) hash.get("fields");
		if (array == null)
			return;
		
		// enumerate all fields, array is an array of hashes
		for (Object fieldObject : array) {
			
			if (fieldObject != null) {
				
				Map<Object, Object> fieldDefinition = (Map<Object, Object>)fieldObject;
				
				String name = (String)fieldDefinition.get("name");
				
				if (name != null) {
					
					if (name.equalsIgnoreCase(BUGZ_FIELD_BUG_ID)) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						mDatabaseToBugz.put(BugzillaDatabase.FIELD_NAME_BUG_ID, field);
						//mBugzToDatabase.put(name, BugzillaDatabase.FIELD_NAME_BUG_ID);
					}
					else if (name.equalsIgnoreCase(BUGZ_FIELD_COMPONENT)) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						mDatabaseToBugz.put(BugzillaDatabase.FIELD_NAME_COMPONENT, field);
						//mBugzToDatabase.put(name, BugzillaDatabase.FIELD_NAME_COMPONENT);
					}
					else if (name.equalsIgnoreCase(BUGZ_FIELD_PRODUCT)) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						mDatabaseToBugz.put(BugzillaDatabase.FIELD_NAME_PRODUCT, field);
						//mBugzToDatabase.put(name, BugzillaDatabase.FIELD_NAME_PRODUCT);
					}
					else if (name.equalsIgnoreCase(BUGZ_FIELD_SHORT_DESCRIPTION)) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						mDatabaseToBugz.put(BugzillaDatabase.FIELD_NAME_SUMMARY, field);
						//mBugzToDatabase.put(name, BugzillaDatabase.FIELD_NAME_SUMMARY);
					}
					else if (name.equalsIgnoreCase(BUGZ_FIELD_LONG_DESCRIPTION)) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						mDatabaseToBugz.put(BugzillaDatabase.FIELD_NAME_COMMENT, field);
						//mBugzToDatabase.put(name, BugzillaDatabase.FIELD_NAME_COMMENT);
					}
					else if (name.equalsIgnoreCase(BUGZ_FIELD_ASSIGNED_TO)) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						mDatabaseToBugz.put(BugzillaDatabase.FIELD_NAME_ASSIGNEE, field);
						//mBugzToDatabase.put(name, BugzillaDatabase.FIELD_NAME_ASSIGNEE);
					}
					else if (name.equalsIgnoreCase(BUGZ_FIELD_CREATOR)) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						mDatabaseToBugz.put(BugzillaDatabase.FIELD_NAME_CREATOR, field);
						//mBugzToDatabase.put(name, BugzillaDatabase.FIELD_NAME_CREATOR);
					}
					else if (name.equalsIgnoreCase(BUGZ_FIELD_CREATED)) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						mDatabaseToBugz.put(BugzillaDatabase.FIELD_NAME_CREATED, field);
						//mBugzToDatabase.put(name, BugzillaDatabase.FIELD_NAME_CREATED);
					}
					else if (name.equalsIgnoreCase(BUGZ_FIELD_MODIFIED)) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						mDatabaseToBugz.put(BugzillaDatabase.FIELD_NAME_MODIFIED, field);
						//mBugzToDatabase.put(name, BugzillaDatabase.FIELD_NAME_MODIFIED);
					}
					else if (name.equalsIgnoreCase(BUGZ_FIELD_STATUS)) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						mDatabaseToBugz.put(BugzillaDatabase.FIELD_NAME_STATUS, field);
						//mBugzToDatabase.put(name, BugzillaDatabase.FIELD_NAME_STATUS);
					}
					else if (name.equalsIgnoreCase(BUGZ_FIELD_PRIORITY)) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						mDatabaseToBugz.put(BugzillaDatabase.FIELD_NAME_PRIORITY, field);
						//mBugzToDatabase.put(name, BugzillaDatabase.FIELD_NAME_PRIORITY);
					}
					else if (name.equalsIgnoreCase(BUGZ_FIELD_SEVERITY)) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						mDatabaseToBugz.put(BugzillaDatabase.FIELD_NAME_SEVERITY, field);
						//mBugzToDatabase.put(name, BugzillaDatabase.FIELD_NAME_SEVERITY);
					}
					else if (name.equalsIgnoreCase(BUGZ_FIELD_RESOLUTION)) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						mDatabaseToBugz.put(BugzillaDatabase.FIELD_NAME_RESOLUTION, field);
						//mBugzToDatabase.put(name, BugzillaDatabase.FIELD_NAME_RESOLUTION);
					}
				}
			}
		}
	}

	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}

	@Override
	public Map<Object, Object> getParameterMap() {
		return Collections.unmodifiableMap(params);
	}

}
