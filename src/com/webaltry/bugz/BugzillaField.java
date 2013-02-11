package com.webaltry.bugz;

import java.util.ArrayList;
import java.util.Map;

public class BugzillaField {
	public enum ValueType {
		UNKNOWN, TEXT, LARGE_TEXT, DROP_DOWN, DATE_TIME
	}

	public String bugzName;
	private ArrayList<String> mBugzValues;
	public ValueType bugzType;
	public int bugzId;

	public BugzillaField(Map<Object, Object> fieldDefinition) {

		String fieldName = (String) fieldDefinition.get("name");
		if (fieldName != null)
			bugzName = new String(fieldName);
		else
			bugzName = new String("");

		Integer id = (Integer) fieldDefinition.get("id");
		if (id != null)
			bugzId = id;
		else
			bugzId = 0;

		Integer type = (Integer) fieldDefinition.get("type");
		if (type != null) {
			switch (type) {
			case 1:
				bugzType = ValueType.TEXT;
				break;
			case 2:
				bugzType = ValueType.DROP_DOWN;
				break;
			case 4:
				bugzType = ValueType.LARGE_TEXT;
				break;
			case 5:
				bugzType = ValueType.DATE_TIME;
				break;
			default:
				bugzType = ValueType.UNKNOWN;
			}

		} else {
			bugzType = ValueType.UNKNOWN;
		}

		if (bugzType == ValueType.DROP_DOWN) {
			Object[] values = (Object[]) fieldDefinition.get("values");
			if (values != null) {
				mBugzValues = new ArrayList<String>();
				for (Object value : values) {
					Map<Object, Object> valueMap = (Map<Object, Object>) value;
					if (valueMap != null) {
						String valueName = (String) valueMap.get("name");
						if (valueName != null) {
							if (!valueName.isEmpty())
								mBugzValues.add(valueName);
						}
					}
				}
			}
		}
	}
	
	public ArrayList<String> getValues() {
		return mBugzValues;
	}

}
