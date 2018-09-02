package org.objectweb.snap.concerns;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import org.objectweb.snap.Context;

import damon.annotation.Abstractions;
import damon.annotation.DistributedAspect;
import damon.annotation.RemoteAdvice;
import damon.annotation.RemotePointcut;
import damon.annotation.SourceHook;
import damon.annotation.Type;
import damon.invokation.RemoteJoinPoint;
import damon.invokation.aspectwerkz.AspectRemoting;
import damon.reflection.thisEndPoint;


/**
 * Distributed Aspect for Adaptive Redirection
 * @author Rubén Mondéjar <ruben.mondejar@urv.cat>
 */
@DistributedAspect(abstraction = Abstractions.LOCAL, target = Context.SNAP_URL_BASE)
public class AdaptiveRedirector extends AspectRemoting {

	private Hashtable<String,String> redirections = new Hashtable<String,String>();
	
	public boolean hasRedirection(String appName) {
		return redirections.containsKey(appName);
	}
	
	@RemoteAdvice(id = "alarm")
	@RemotePointcut(id = "findHost", abstraction = Abstractions.ANY, synchro = true, lazy = true)	
	public void updateRedirections(RemoteJoinPoint rjp, String appName, boolean on) {
		if (appName!=null) {
		if (on) {
			String httpURL = (String) super.invoke("updateRedirections", null, new Object[] { appName });	
			if (httpURL!=null) redirections.put(appName, httpURL);
		}
		else redirections.remove(appName);
		}
	}	
	
		
	@SourceHook(source = "org.objectweb.snap.concerns.SourceHooks", method = "service", type = Type.AROUND)		
	public Object redirect(JoinPoint joinPoint) throws Throwable {
		
		HttpServletRequest httpReq =(HttpServletRequest) thisEndPoint.getParams(joinPoint)[0];
		HttpServletResponse httpRes =(HttpServletResponse) thisEndPoint.getParams(joinPoint)[1];
		
		String uri = httpReq.getRequestURI();
  		String appName = getFirstName(uri);
  		  
		//redirect to other instance?		
  		if (hasRedirection(appName)) {  	      
  		  
  		  String dest = redirections.get(appName);  		  		 	    
  		  String params = composeParams(httpReq);
  		  
  		  //new url
  	      dest += params;
  	      if (!params.equals("")) dest += '?' + params;
  	      
   	      httpRes.sendRedirect(dest);  
  	      	  
  	    }
  		  		
  		Object nothing = null;
  		try {
  		nothing =  joinPoint.proceed();
  		} catch (java.lang.IllegalStateException ise) { 
            //committed response exception
  		}
  		return nothing;
	}
	
	private String getFirstName(String uri) {
		
		StringTokenizer st = new StringTokenizer(uri,"/");
		String appName = Context.SNAP_CONTEXT;
		if (st.hasMoreTokens()) appName = st.nextToken();		
		return appName;
	}

	private String composeParams(javax.servlet.ServletRequest req) {
		
		  String params = "";
  	      Map map = req.getParameterMap();  		
  	      Enumeration names = req.getParameterNames();
  	  	 
  	      while (names.hasMoreElements()){
  	  		String name = (String) names.nextElement();  	  		
  	  		String[] attr = (String[]) map.get(name);
  	  		params += name+'='+attr[0];
  	  		if (names.hasMoreElements()) params += '&';  	  		  
  	      } 
  	      return params;
	}
		
}	