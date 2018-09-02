package org.objectweb.snap.concerns;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import org.objectweb.snap.Context;
import org.objectweb.snap.util.HashHashtable;

import damon.annotation.Abstractions;
import damon.annotation.DistributedAspect;
import damon.annotation.RemoteAdvice;
import damon.annotation.RemotePointcut;
import damon.annotation.SourceHook;
import damon.annotation.Type;
import damon.invokation.RemoteJoinPoint;
import damon.invokation.aspectwerkz.AspectRemoting;
import damon.reflection.thisEndPoint;
import easypastry.dht.DHTException;
import easypastry.dht.DHTHandler;


/**
 * This distributed aspect enable Shared Sessions in a Snap application
 * @author Rubén Mondéjar <ruben.mondejar@urv.cat>
 */
@DistributedAspect(abstraction = Abstractions.LOCAL, target = Context.SNAP_URL_BASE)
public class SharedSession extends AspectRemoting {
		
	private final String JSESSSION_ID = "jsessionid";
	private HashHashtable<String,String,Object> cache = new HashHashtable<String,String,Object>();
	
	@RemotePointcut(id = "session_attribute", abstraction = Abstractions.HOPPED)	
	@SourceHook(source = "org.objectweb.snap.concerns.SourceHooks", method = "setSession", type = Type.AFTER)	
	public void propagate(JoinPoint joinPoint) throws Throwable {
		  Object[] params = thisEndPoint.getParams(joinPoint);
		  String key = (String) params[0];
		  super.invoke("propagate", joinPoint, key, params);		  
		  storeSessionData(key, new Object[]{params[1],params[2]});	
	}

	@RemoteAdvice(id = "session_attribute")
	public void executeUpdate(RemoteJoinPoint rjp, String jsid, String name, Object value)  {		
		System.out.println("Shared Session : Received Attributed (" + jsid + ","+ name + ")");
		cache.put(jsid, name, value);
	}
	
	@SourceHook(source = "org.objectweb.snap.concerns.SourceHooks", method = "service", type = Type.AROUND)	
	public Object servletService(JoinPoint joinPoint) throws Throwable {
		
		HttpServletRequest req =(HttpServletRequest)thisEndPoint.getParams(joinPoint)[0];
		HttpServletResponse res = (HttpServletResponse) thisEndPoint.getParams(joinPoint)[1];
		
		String uri = req.getRequestURI();		
		
		if (uri.endsWith(".jsp") || uri.endsWith(".html") || uri.endsWith(".htm")) {		
		  	
		  String sid = req.getRequestedSessionId();		  
		  String sidParam = req.getParameter(JSESSSION_ID);
		  HttpSession session = req.getSession(false);	//no create session
	
		  //if (jsessionid==null) {
		  //	  if (jsessionidParam!=null) jsessionid = jsessionidParam;
		  //}
		  if (sid==null) sid = sidParam;
	  
		  if (sid!=null) {		  
			  
			//if url do not have param, then attach it  
			if (sidParam==null) {
				  String url = generateURL(req.getRequestURL().toString(), getRequestParams(req),sid); 	        
	  	          res.sendRedirect(url);
	  	          return null;
			}  
                    	
			//if no session, then create it and restore data
            if (session==null) {        
		      //System.out.println("jsessionid ("+sid+"), no session");		    
		      session = req.getSession(); //create session
		      restoreSessionData(sid,session);		    
            }	
            
            //if session exists and is not the original, then restore data
            else {         	  
			  //System.out.println("jsessionid ("+sid+"), session ("+session.getId()+")");			
			  if (!sid.equals(session.getId())) {			  
			    restoreSessionData(sid,session);			  
			  }
		    }
		  }  
		  //else {error}
		  
		}			
	  
      return joinPoint.proceed();  		
	}
	
	private void storeSessionData(String jsessionid, Object[] sessionData) {
			
		 try {
			DHTHandler dht = thisEndPoint.getPersistenceHandler(context);
			dht.put(jsessionid, sessionData);
		} catch (DHTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void restoreSessionData(String jsessionid, HttpSession session) {		
		Hashtable<String,Object> data = cache.get(jsessionid);
		if (data==null) {
			try {
			  DHTHandler dht = thisEndPoint.getPersistenceHandler(context);
			  dht.get(jsessionid);
			} catch (DHTException pe) {}		
		}
		else {
		  for (String name : data.keySet() ) {		  
  		    session.setAttribute(name, data.get(name));
	      }		  
		}
		
	}
	
	private String generateURL(String url, String params, String jsessionid) {
		//attach jsessionid param
		  String dest = url;		  
    	  if (params.equals("") || params.length()<1) dest +=	"?"+JSESSSION_ID+"=" + jsessionid;
    	  else dest +=	"?" + params + "&"+JSESSSION_ID+"=" + jsessionid;
    	  System.out.println("> "+url+" + "+params+" + "+jsessionid+" = "+dest);
		  return dest;
	}

	private String getRequestParams(javax.servlet.ServletRequest req) {
	    return getRequestParams(req, new String[]{});
	}
	
	private String getRequestParams(ServletRequest req, String ... exceptions) {
		  String params = "";
	      Map map = req.getParameterMap();  		
	      Enumeration names = req.getParameterNames();
	  	 
	      while (names.hasMoreElements()){
	  		String name = (String) names.nextElement();
	  		//is an exception?
	  		boolean exception = false;
	  		for (String ex : exceptions) {
	  		  exception |= name.equals(ex);	
	  		}	  		
	  		
	  		if (!exception) {
	  		  String[] attr = (String[]) map.get(name);
	  		  params += name+'='+attr[0];
	  		  if (names.hasMoreElements()) params += '&';
	  		}  
	      } 
	      return params;
	}
		
}	