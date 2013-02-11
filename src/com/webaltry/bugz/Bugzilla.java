
package com.webaltry.bugz;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.BugzillaException;
import com.j2bugzilla.base.ConnectionException;
import com.j2bugzilla.base.Product;
import com.j2bugzilla.rpc.BugSearch;
import com.j2bugzilla.rpc.GetAccessibleProducts;
import com.j2bugzilla.rpc.GetLegalValues;
import com.j2bugzilla.rpc.GetProduct;
import com.j2bugzilla.rpc.LogIn;

public class Bugzilla {
    
    private static final String TAG = Bugzilla.class.getSimpleName();
    
    /** for communicating with the bugzilla server */
    private BugzillaConnector mBugzilla;
   
    private String mErrorMessage;
    private Map<String, BugzillaField> mBugzFields;
    private ArrayList<Product> mBugzProducts;
    private ArrayList<String> mBugzPeople;

    /**
     * @param loginServer
     * @param loginUser
     * @param loginPassword
     */
    public void connect(String loginServer, String loginUser, String loginPassword) {

        if (mBugzilla != null)
            mBugzilla = null;

         mErrorMessage = null;

        try
        {
        	/* log into bugz */
            mBugzilla = new BugzillaConnector();
            mBugzilla.connectTo(loginServer);
            LogIn logIn = new LogIn(loginUser, loginPassword);
            mBugzilla.executeMethod(logIn);
           
            /* get field information */
            BugzillaFields getLegal = new BugzillaFields();
            mBugzilla.executeMethod(getLegal);
            mBugzFields = getLegal.getFields();

            /* get product information */
            mBugzProducts = new ArrayList<Product>();
            GetAccessibleProducts products = new GetAccessibleProducts();
            mBugzilla.executeMethod(products);
            int[] ids = products.getProductIDs();
            for (int id : ids) {
                GetProduct getProduct = new GetProduct(id);
                mBugzilla.executeMethod(getProduct);
                Product product = getProduct.getProduct();
                mBugzProducts.add(product);
            }
            
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

    public boolean isConnected() {
        return mBugzilla != null;
    }

    void disconnect() {

        mBugzilla = null;
        mBugzFields = null;
        mBugzProducts = null;
        mBugzPeople = null;
    }
    
    public ArrayList<String> getValues(String field) {
    	
    	/* product names handled specially */
    	if (field.equalsIgnoreCase(BugzillaDatabase.FIELD_NAME_PRODUCT)) {
    		
    		if (mBugzProducts == null)
    			return null;
    		
    		ArrayList<String> products = new ArrayList<String>();
    		
            for (Product product : mBugzProducts) {
            	
            	products.add(product.getName());
            }
		
    		return products;
    	}
    	
    	/* people names handled specially */
    	if (field.equalsIgnoreCase(BugzillaDatabase.FIELD_NAME_ASSIGNEE) || field.equalsIgnoreCase(BugzillaDatabase.FIELD_NAME_CREATOR)) {
    		return mBugzPeople;
    	}
    	
    	/* all others come from bugz server */
    	if (mBugzFields == null)
    		return null;
    	
    	BugzillaField bugzField = mBugzFields.get(field);
    	if (bugzField == null)
    		return null;
    	
    	return bugzField.getValues();
    }
 }
