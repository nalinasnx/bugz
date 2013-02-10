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
	
	private final Map<String, BugzillaField> fields = new HashMap<String, BugzillaField>();

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


	//private Set<String> legalValues = Collections.emptySet();

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
		//params.put("names", field);
	}

	/**
	 * Returns the {@code Set} of legal strings which the given {@link Fields
	 * field} may be assigned.
	 * 
	 * @return A set of {@code Strings}.
	 */
	//public Set<String> getLegalValues() {
	//	return legalValues;
	//}

	public Map<String, BugzillaField> getFields() {
		return fields;
	}

	@Override
	public void setResultMap(Map<Object, Object> hash) {
		
		//legalValues = new HashSet<String>();

		Object[] array = (Object[]) hash.get("fields");
		if (array == null)
			return;
		
		
		// enumerate all fields, array is an array of hashes
		for (Object fieldObject : array) {
			
			if (fieldObject != null) {
				
				Map<Object, Object> fieldDefinition = (Map<Object, Object>)fieldObject;
				
				String name = (String)fieldDefinition.get("name");
				
				if (name != null) {
					
					if (name.equalsIgnoreCase("bug_id")) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						fields.put(BugzillaDatabase.FIELD_NAME_BUG_ID, field);
					}
					else if (name.equalsIgnoreCase("component")) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						fields.put(BugzillaDatabase.FIELD_NAME_COMPONENT, field);
					}
					else if (name.equalsIgnoreCase("product")) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						fields.put(BugzillaDatabase.FIELD_NAME_PRODUCT, field);
					}
					else if (name.equalsIgnoreCase("short_desc")) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						fields.put(BugzillaDatabase.FIELD_NAME_SUMMARY, field);
					}
					else if (name.equalsIgnoreCase("longdesc")) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						fields.put(BugzillaDatabase.FIELD_NAME_COMMENT, field);
					}
					else if (name.equalsIgnoreCase("assigned_to")) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						fields.put(BugzillaDatabase.FIELD_NAME_ASSIGNEE, field);
					}
					else if (name.equalsIgnoreCase("reporter")) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						fields.put(BugzillaDatabase.FIELD_NAME_CREATOR, field);
					}
					else if (name.equalsIgnoreCase("creation_ts")) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						fields.put(BugzillaDatabase.FIELD_NAME_CREATED, field);
					}
					else if (name.equalsIgnoreCase("delta_ts")) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						fields.put(BugzillaDatabase.FIELD_NAME_MODIFIED, field);
					}
					else if (name.equalsIgnoreCase("bug_status")) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						fields.put(BugzillaDatabase.FIELD_NAME_STATUS, field);
					}
					else if (name.equalsIgnoreCase("priority")) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						fields.put(BugzillaDatabase.FIELD_NAME_PRIORITY, field);
					}
					else if (name.equalsIgnoreCase("bug_severity")) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						fields.put(BugzillaDatabase.FIELD_NAME_SEVERITY, field);
					}
					else if (name.equalsIgnoreCase("resolution")) {
						
						BugzillaField field = new BugzillaField(fieldDefinition);
						fields.put(BugzillaDatabase.FIELD_NAME_RESOLUTION, field);
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
