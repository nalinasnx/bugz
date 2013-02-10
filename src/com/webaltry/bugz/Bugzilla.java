
package com.webaltry.bugz;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.BugzillaException;
import com.j2bugzilla.base.ConnectionException;
import com.j2bugzilla.rpc.BugSearch;
import com.j2bugzilla.rpc.GetLegalValues;
import com.j2bugzilla.rpc.LogIn;

public class Bugzilla {
    
    private static final String TAG = Bugzilla.class.getSimpleName();
    
    /** for communicating with the bugzilla server */
    private BugzillaConnector mBugzilla;
   
    private String mLoginUser;
    private String mErrorMessage;
    private Set<String> values;
    
    private Map<String, BugzillaField> mBugzFields;
   

    /**
     * @param loginServer
     * @param loginUser
     * @param loginPassword
     */
    public void connect(String loginServer, String loginUser, String loginPassword) {

        if (mBugzilla != null)
            mBugzilla = null;

        mLoginUser = loginUser;
        mErrorMessage = null;

        try
        {
            mBugzilla = new BugzillaConnector();
            mBugzilla.connectTo(loginServer);
            LogIn logIn = new LogIn(loginUser, loginPassword);
            mBugzilla.executeMethod(logIn);
           
            BugzillaFields getLegal = new BugzillaFields();
            mBugzilla.executeMethod(getLegal);
            
            mBugzFields = getLegal.getFields();
            
        } catch (ConnectionException e) {

            disconnect();
            e.printStackTrace();
            
            Throwable cause = e.getCause();
            if (cause != null)
            	mErrorMessage = cause.getMessage();
            else
            	mErrorMessage = e.getMessage();
            
        } catch (BugzillaException e) {

            disconnect();
            e.printStackTrace();
            
            Throwable cause = e.getCause();
            if (cause != null)
            	mErrorMessage = cause.getMessage();
            else
            	mErrorMessage = e.getMessage();
            
        } catch (Exception e) {

            disconnect();
            e.printStackTrace();
            
            Throwable cause = e.getCause();
            if (cause != null)
            	mErrorMessage = cause.getMessage();
            else
            	mErrorMessage = e.getMessage();
         }
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    ArrayList<Bug> searchBugs(String assignedTo) {

        Log.d(TAG, "searchBugs");
        // ArrayList<Bug> bugs = new ArrayList<Bug>();
        //
        // Map<String, Object> bugData = new HashMap<String, Object>();
        // BugFactory factory = new BugFactory();
        //
        // bugData.put("id", 12386);
        // bugData.put("product", "product 1");
        // bugData.put("component", "component 1");
        // bugData.put("summary", "summary 1");
        // bugData.put("version", "version 1");
        // Bug bug = factory.createBug(bugData);
        // bugs.add(bug);
        // return bugs;

        try
        {
        	BugzillaSearch search = new BugzillaSearch();
        	
        	search.addParameter("assigned_to", assignedTo);

            mBugzilla.executeMethod(search);
            
            return (ArrayList<Bug>) search.getSearchResults();
            
        } catch (BugzillaException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    ArrayList<Bug> searchBugs(ArrayList<QueryConstraint> constraints) {

    	if (!isConnected())
    		return null;
        Log.d(TAG, "searchBugs");
        // ArrayList<Bug> bugs = new ArrayList<Bug>();
        //
        // Map<String, Object> bugData = new HashMap<String, Object>();
        // BugFactory factory = new BugFactory();
        //
        // bugData.put("id", 12386);
        // bugData.put("product", "product 1");
        // bugData.put("component", "component 1");
        // bugData.put("summary", "summary 1");
        // bugData.put("version", "version 1");
        // Bug bug = factory.createBug(bugData);
        // bugs.add(bug);
        // return bugs;

        try
        {
        	BugzillaSearch search = new BugzillaSearch(constraints);

            mBugzilla.executeMethod(search);
            
            return (ArrayList<Bug>) search.getSearchResults();
            
        } catch (BugzillaException e) {
            e.printStackTrace();
        }

        return null;
    }

    boolean isConnected() {
        return mBugzilla != null;
    }

    void disconnect() {

        mBugzilla = null;
    }

    String getUser() {
        return mLoginUser;
    }
}
