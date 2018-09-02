package org.objectweb.snap.concerns;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import org.objectweb.snap.Application;
import org.objectweb.snap.Context;
import org.objectweb.snap.util.Requests;

import damon.annotation.Abstractions;
import damon.annotation.DistributedAspect;
import damon.annotation.RemoteAdvice;
import damon.annotation.RemoteCondition;
import damon.annotation.RemotePointcut;
import damon.annotation.SourceHook;
import damon.annotation.Type;
import damon.invokation.RemoteJoinPoint;
import damon.invokation.aspectwerkz.AspectRemoting;
import damon.reflection.thisEndPoint;

@DistributedAspect(abstraction = Abstractions.LOCAL, target = Context.SNAP_URL_BASE)
public class HostMonitor extends AspectRemoting {

	private final int MAX_REQ_PER_SEC = 400;
	private final int MIN_REQ_PER_SEC = 100;
	
	private Set<String> alarms = new HashSet<String>();
	
	public boolean hasAlarm(String appName) {
		return alarms.contains(appName);
	}
		
	@RemotePointcut(id = "alarm", abstraction = Abstractions.LOCAL, synchro = false, lazy = true)
	@SourceHook(source = "org.objectweb.snap.concerns.SourceHooks", method = "service", type = Type.AFTER)
	public void sensor(JoinPoint joinPoint) {
		
		HttpServletRequest httpReq =(HttpServletRequest) thisEndPoint.getParams(joinPoint)[0];		
		String appName = getFirstName(httpReq.getRequestURI());				
		Application app = Application.getApp(appName);
			
		if (app!=null) {
		  Requests reqs = app.getRequests();				
		  long rps = reqs.requestArrive();	
		
		  //System.out.println(System.currentTimeMillis()+" -> "+rps);
		  if (rps>MAX_REQ_PER_SEC && !alarms.contains(appName)) {
			System.out.println(appName+" ALARM PEAK DETECTED!!!");			
			alarms.add(appName);			
			super.invoke("sensor", joinPoint, new Object[]{appName,true});			
		  }
		  if (alarms.contains(appName) && rps<MIN_REQ_PER_SEC) {
			alarms.remove(appName);
			super.invoke("sensor", joinPoint, new Object[]{appName,false});
		  }
		}		
	}

	private String getFirstName(String uri) {
		
		StringTokenizer st = new StringTokenizer(uri,"/");
		String appName = Context.SNAP_CONTEXT;
		if (st.hasMoreTokens()) appName = st.nextToken();		
		return appName;
	}
	
	@RemoteCondition(id = "findHost")
	public boolean existApp(String appName) {	
		Application app = Application.getApp(appName);	
		System.out.println("----> findHost Condition: "+(app!=null && !hasAlarm(appName)));
		return app!=null && !hasAlarm(appName);
	}
	
	@RemoteAdvice(id = "findHost")	
	public void findApp(RemoteJoinPoint rjp, String appName) {
	  Application app = Application.getApp(appName);
	  System.out.println("----> findHost Advice : "+(app!=null));
	  String httpURL = "http://"+app.getServerName()+":"+app.getServerPort()+"/"+appName;
	  rjp.proceed(httpURL);	  
	}
	
	
}