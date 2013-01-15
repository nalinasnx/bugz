package com.webaltry.bugz;

//import com.j2bugzilla.base.BugzillaConnector;
//import com.webaltry.bugz.bugzilla.BugzillaServer;
import com.webaltry.bugz.BugzillaServiceHelper;

import android.app.Application;

public class BugzillaApplication extends Application {
	
	private Bugzilla bugzilla;
    private BugzillaServiceHelper bugzillaServiceHelper;
    
    @Override
    public void onCreate() {
    	
    	super.onCreate();
    	
    	bugzilla = new Bugzilla();
    	bugzillaServiceHelper = new BugzillaServiceHelper(this);
    	
     }
    
    /** 
     * Returns the global instance of the Bugzilla object,
     * used to access the bugzilla server.
     * <p>
     *
     * @param 
     * @return          the Bugzilla object
     */
    public Bugzilla getBugzilla() {
    	return bugzilla;
    }
    
    public BugzillaServiceHelper getBugzillaServiceHelper() {
    	return bugzillaServiceHelper;
    }

}
