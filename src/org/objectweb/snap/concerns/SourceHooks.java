package org.objectweb.snap.concerns;

import org.codehaus.aspectwerkz.annotation.*;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import damon.reflection.thisEndPoint;

public class SourceHooks {	
	
	
	//LOCATOR
	
	@Before("execution(* org.objectweb.snap.Application.resolveP2PURL(..)) AND args(p2purl)")
	public void resolve(JoinPoint joinPoint, String p2purl) throws Throwable {
		//System.out.println("resolveP2PURL SourceHook (" + p2purl+ ")");		
		thisEndPoint.addParams(joinPoint, p2purl);
	}
	
	
	//REQUEST	
	
	@Before("execution(* javax.servlet.http.HttpServlet.service(..)) AND args(req,res)")	
	public void service(JoinPoint joinPoint, javax.servlet.ServletRequest req, javax.servlet.ServletResponse res) throws Throwable {
		//System.out.println("Servlet Service (" + req+ ")");
		//System.out.println(System.currentTimeMillis());
		thisEndPoint.addParams(joinPoint, req, res);
		
	}
	
	//SESSION
	
	@Before("call(* javax.servlet.http.HttpSession.setAttribute(..)) AND args(name,value)")	
	public void setSession(JoinPoint joinPoint, String name, Object value) throws Throwable {		
		javax.servlet.http.HttpSession session = (javax.servlet.http.HttpSession) joinPoint.getCallee();		 		
		//System.out.println("Session (" + session.getId()+ ") setAttribute("+name+","+value+")");		
		thisEndPoint.addParams(joinPoint, session.getId(), name, value);		
	}

	
	//APPLICATION
	
	@Before("call(* javax.servlet.ServletContext.setAttribute(..)) AND args(key, value)")
	public void setAttribute(JoinPoint joinPoint, String key, Object value) throws Throwable {		
		  thisEndPoint.addParams(joinPoint, key, value);		
	}
	
	@Before("call(* javax.servlet.ServletContext.getAttribute(..)) AND args(key)")
	public void getAttribute(JoinPoint joinPoint, String key) throws Throwable {
		  thisEndPoint.addParams(joinPoint, key);		  
	}
	
	
	//DATABASE
			
	@Before("call(* java.sql.Statement.executeUpdate(..)) AND args(statement)")
	public void executeUpdate(JoinPoint joinPoint, String statement) throws Throwable {		
		//System.out.println("executeUpdate SourceHook (" + statement+ ")");		
		thisEndPoint.addParams(joinPoint, statement);
	}
	
	@Before("call(* java.sql.Statement.executeQuery(..)) AND args(query)")	
	public void executeQuery(JoinPoint joinPoint, String query) throws Throwable {
		//System.out.println("executeQuery SourceHook (" + query+ ")");		
		thisEndPoint.addParams(joinPoint, query);
	}
	
}
