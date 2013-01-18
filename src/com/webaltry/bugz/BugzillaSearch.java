package com.webaltry.bugz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugFactory;
import com.j2bugzilla.base.BugzillaMethod;
import com.j2bugzilla.rpc.BugSearch;

public class BugzillaSearch implements BugzillaMethod {

	private final Map<Object, Object> params = new HashMap<Object, Object>();

	/**
	 * The method Bugzilla will execute via XML-RPC
	 */
	private static final String METHOD_NAME = "Bug.search";
	
	/**
	 * A {@code Map} returned by the XML-RPC method.
	 */
	private Map<Object, Object> hash = new HashMap<Object, Object>();
	
	public BugzillaSearch() {
		
	}
	public BugzillaSearch(ArrayList<QueryConstraint> constraints) {
        for (QueryConstraint constraint : constraints) {
        	params.put(constraint.field, constraint.value);
        }
	}
	public void addParameter(String name, String value) {
		params.put(name, value);
	}
	/**
	 * Returns the {@link Bug Bugs} found by the query as a <code>List</code>
	 * @return a {@link List} of {@link Bug Bugs} that match the query and limit
	 */
	public List<Bug> getSearchResults() {
		List<Bug> results = new ArrayList<Bug>();
		/*
		 * The following is messy, but necessary due to how the returned XML document nests
		 * Maps.
		 */
		if(hash.containsKey("bugs")) {
			Object[] bugs = (Object[])hash.get("bugs");
			
			for(Object o : bugs) {
				@SuppressWarnings("unchecked")
				Map<String, Object> bugMap = (HashMap<String, Object>)o;
				//Handle version property for older Bugzillas which did not include it in the public portion of the hash
				if(!bugMap.containsKey("version")) {
					Map<?, ?> internals = (Map<?, ?>) bugMap.get("internals");
					bugMap.put("version", internals.get("version"));
				}
				Bug bug = new BugFactory().createBug(bugMap);
				results.add(bug);
			}
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResultMap(Map<Object, Object> hash) {
		this.hash = hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Object, Object> getParameterMap() {
		return Collections.unmodifiableMap(params);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}
	
}
