package org.objectweb.snap.concerns;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.objectweb.snap.Application;
import org.objectweb.snap.Context;

import damon.annotation.Abstractions;
import damon.annotation.DistributedAspect;
import damon.annotation.RemoteAdvice;
import damon.annotation.RemoteCondition;
import damon.annotation.RemoteMetaPointcut;
import damon.annotation.RemotePointcut;
import damon.annotation.Type;
import damon.invokation.RemoteJoinPoint;
import damon.metalevel.aspectwerkz.DistributedMetaAspect;

@DistributedAspect(abstraction = Abstractions.LOCAL, target = Context.SNAP_URL_BASE)
public class DynamicActivator extends DistributedMetaAspect {

	private Hashtable<String,Set<String>> membership = new Hashtable<String,Set<String>>();
	
	private Set<String> alarms = new HashSet<String>();
	
	public boolean hasAlarm(String appName) {
		return alarms.contains(appName);
	}	
	
    @RemoteMetaPointcut(id = "alarm", type = Type.BEFORE)     
	public void alarm(RemoteJoinPoint rjp, String appName, boolean on) {	   
	   if (on) alarms.add(appName);
	   else alarms.remove(appName);
	}
    
    @RemoteMetaPointcut(id = "findHost", type = Type.AFTER, ack = true)
    @RemotePointcut(id = "activate", abstraction = Abstractions.ANY) 
    //@RemoteMetaAdvice(id = "hostFound", abstraction = Abstractions.LOCAL, lazy = true)    
	public void activation(RemoteJoinPoint rjp, String appName, Object result) {
    	String dest = (String) result;    	   	
    	if (dest==null) {
    	  dest = getMember(appName);	
    	  if (dest==null) {    		
    		Application app = Application.getApp(appName);
    		try {
    		  app.reinsert();
    		} catch(Exception e)  {
    			//e.printStackTrace();
    		}
    		System.out.println("DynamicActivator [starting activation]");
    		super.invoke("activation", null, new Object[] { appName });
    	  }
    	//  else {
      	//	super.supplantation("activationPipe", rjp, appName, dest);
      	//  }
        }
    	    	  
	}       

	@RemoteCondition(id = "activate")
	public boolean notExistApp(String appName) {	

		Application app = Application.getApp(appName);
		System.out.println("----> activate Condition: "+(app==null && !hasAlarm(appName)));
		
		//is activating??
		return app==null && !hasAlarm(appName);
	}	

	@RemoteAdvice(id = "activate")
	@RemotePointcut(id = "activationSuccess", abstraction = Abstractions.DIRECT)
	public void deployApp(RemoteJoinPoint rjp, String appName) {
	  
		System.out.println("----> activate Deploy App: "+appName);
	  
	    String httpURL = deploy(appName);  
		if (httpURL!=null) super.invoke("deployApp", null, rjp.getOriginator(), new Object[] { appName,  httpURL});
		else System.out.println("Dynamic Activator [activation cancelled]");
	  
	}
	  
	private synchronized String deploy(String appName) {
		
		String httpURL = null;		
		Application app = Application.getApp(appName);
		
		if (app==null) {
		
		  try {		  
			Application snap = Application.getSnapApp();  
			snap.activateSnapApp(Context.SNAP_URL + appName);
		  } catch(Exception e) {
			  e.printStackTrace();
			  return null;
		  }	  
		  
		  int retries = 0;
		  while (app==null && retries < 60) {			  
			try {
				Thread.sleep(1000);		
			    System.out.print(":");
			} catch (Exception e) {}
			app = Application.getApp(appName);
			retries++;
			}
		}
		if (app!=null) httpURL = app.getAppHttpUrl();
		
		return httpURL;
	  
	  
	}
	
    @RemoteAdvice(id = "activationSuccess")
    //@RemoteMetaAdvice(id = "hostFound", abstraction = Abstractions.LOCAL, lazy = true)
	public void newHostFound(RemoteJoinPoint rjp, String appName, String dest) {
    	    	
    	if (dest!=null) {
    		        		
    		    updateMembership(appName,dest);   		    
    		    
    			System.out.println("newHostFound supplantation : "+dest);
    			//super.supplantation("newHostFound", rjp, appName, dest);
    					    		
    	}
	}

    
    /*********** MEMBERSHIP HANDLING ***********/
    
	private void updateMembership(String appName, String dest) {
		
		Set<String> members = null;
		if (membership.containsKey(appName)) {
			members = membership.get(appName);
		}
		else {
			members = new HashSet<String>();
		}
		
		members.add(dest);
		membership.put(appName, members);		
	}
	
	
	private String getMember(String appName) {
		
		Set<String> members = membership.get(appName);
		if (members!=null && members.iterator().hasNext()) {
			return members.iterator().next();
		}	
		return null;
	}
				
}