package org.objectweb.snap.concerns;

import org.codehaus.aspectwerkz.joinpoint.JoinPoint;

import org.objectweb.snap.Application;
import org.objectweb.snap.Context;
import org.objectweb.snap.exception.ApplicationDeploymentException;
import org.objectweb.snap.exception.ApplicationException;

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
public class FrontSideLoadBalancer extends AspectRemoting {
		
    @RemotePointcut(id = "resolve", abstraction = Abstractions.ANY, synchro = true, lazy = true)
	@SourceHook(source = "org.objectweb.snap.concerns.SourceHooks", method = "resolve", type = Type.AROUND)
	public Object search(JoinPoint joinPoint) throws Throwable {
		
		String p2purl = (String) thisEndPoint.getParams(joinPoint)[0];
		
		if (existApp(p2purl)) {
			return joinPoint.proceed();
		}		
		
		//System.out.println("Front-Side Load-Balancer : Searching (" + p2purl + ")");
				
		String httpurl = (String) super.invoke("search", joinPoint,	new Object[] { p2purl });
		
		if (httpurl == null) {
			System.out.println("Front-Side Load-Balancer : Not Found, Deploying locally");
			//local deploy
			return joinPoint.proceed();
		} else {
			//System.out.println("Front-Side Load-Balancer : App Found at "+httpurl);
			//remote url
			return httpurl;
		}
	}
	
	@RemoteCondition(id = "resolve")
	public boolean existApp(String p2purl) {
		
		//System.out.println("Front-Side Load-Balancer : Remote Condition for "+p2purl);
		
		p2purl = p2purl.substring(6, p2purl.length());

		int endDomain = p2purl.indexOf("/");
		if (endDomain < 0) endDomain = p2purl.length();

		String appName = p2purl.substring(0, endDomain);

		Application app = Application.getApp(appName);
		
		//if (app!=null) System.out.println("Front-Side Load-Balancer : Found");
		//else System.out.println("Front-Side Load-Balancer : Not Found");
		return app!=null;
	}

	@RemoteAdvice(id = "resolve")
	public void resolveP2PURL(RemoteJoinPoint rjp, String p2purl) throws ApplicationDeploymentException, ApplicationException {
			
		//System.out.println("Front-Side Load-Balancer : Remote Resquest for "+p2purl);
		
		p2purl = p2purl.substring(6, p2purl.length());

		int endDomain = p2purl.indexOf("/");
		if (endDomain < 0) endDomain = p2purl.length();

		String appName = p2purl.substring(0, endDomain);

		String document = "";
		if (endDomain > 0) {
			document = p2purl.substring(endDomain, p2purl.length());		
		}

		Application app = Application.getApp(appName);
		
		System.out.println("Front-Side Load-Balancer : Return http://" + app.getServerName() + ":" + app.getServerPort() + "/" + appName + "/" + document);

		rjp.proceed("http://" + app.getServerName() + ":" + app.getServerPort() + "/" + appName + "/" + document);
	}

}
