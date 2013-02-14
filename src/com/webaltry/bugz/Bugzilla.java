
package com.webaltry.bugz;

import java.util.ArrayList;
import java.util.Map;

import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.BugzillaException;
import com.j2bugzilla.base.ConnectionException;
import com.j2bugzilla.base.Product;
import com.j2bugzilla.rpc.GetAccessibleProducts;
import com.j2bugzilla.rpc.GetProduct;
import com.j2bugzilla.rpc.LogIn;

public class Bugzilla {
    
    /** for communicating with the bugzilla server */
    private BugzillaConnector mBugzilla;
   
    private String mErrorMessage;
    private Map<String, BugzillaField> mDatabaseToBugz;
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
            mDatabaseToBugz = getLegal.getDatabaseToBugz();

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

    ArrayList<BugzillaBug> searchBugs(ArrayList<QueryConstraint> constraints) {

    	if (!isConnected())
    		return null;
    	
        try
        {
        	BugzillaSearch search = new BugzillaSearch();
        	
          for (QueryConstraint constraint : constraints) {
            	
        	  String values[] = constraint.getValues();
        	  if (values == null)
        		  continue;
        	  if (values.length == 0)
        		  continue;
        	  
            	String parameterName = null;
            	
            	if (constraint.databaseFieldName.equals(BugzillaDatabase.FIELD_NAME_PRODUCT))
            		parameterName = "product";
            	else if (constraint.databaseFieldName.equals(BugzillaDatabase.FIELD_NAME_ASSIGNEE))
            		parameterName = "assigned_to";
                else if (constraint.databaseFieldName.equals(BugzillaDatabase.FIELD_NAME_CREATOR))
            		parameterName = "reporter";
                else {
                	BugzillaField field = mDatabaseToBugz.get(constraint.databaseFieldName);
                	if (field != null)
                		parameterName = field.bugzName;
                }
            	
            	
            	
            	if (parameterName == null)
            		continue;
            	
            	if (values.length > 1)
            		search.addParameter(parameterName, values);
            	else
            		search.addParameter(parameterName, values[0]);
            }
        	
        	

            mBugzilla.executeMethod(search);
            
            return (ArrayList<BugzillaBug>) search.getSearchResults2();
            
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
        mDatabaseToBugz = null;
        mBugzProducts = null;
        mBugzPeople = null;
    }
    
    public ArrayList<String> getValues(String databaseFieldName) {
    	
    	/* product names handled specially */
    	if (databaseFieldName.equalsIgnoreCase(BugzillaDatabase.FIELD_NAME_PRODUCT)) {
    		
    		if (mBugzProducts == null)
    			return null;
    		
    		ArrayList<String> products = new ArrayList<String>();
    		
            for (Product product : mBugzProducts) {
            	
            	products.add(product.getName());
            }
		
    		return products;
    	}
    	
    	/* people names handled specially */
    	if (databaseFieldName.equalsIgnoreCase(BugzillaDatabase.FIELD_NAME_ASSIGNEE) || databaseFieldName.equalsIgnoreCase(BugzillaDatabase.FIELD_NAME_CREATOR)) {
    		return mBugzPeople;
    	}
    	
    	/* all others come from bugz server */
    	if (mDatabaseToBugz == null)
    		return null;
    	
    	BugzillaField bugzField = mDatabaseToBugz.get(databaseFieldName);
    	if (bugzField == null)
    		return null;
    	
    	return bugzField.getValues();
    }
 }
