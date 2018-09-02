package org.objectweb.snap.concerns;

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

@DistributedAspect(abstraction = Abstractions.LOCAL, target = Context.SNAP_URL_BASE)
public class DistributedDatabase extends AspectRemoting {
		
	@RemotePointcut(id = "database_update", abstraction = Abstractions.MULTI)
	@SourceHook(source = "org.objectweb.snap.concerns.SourceHooks", method = "executeUpdate", type = Type.AFTER)	
	public void replicate(JoinPoint joinPoint) throws Throwable {
		  String statement = (String) thisEndPoint.getParams(joinPoint)[0];		
		  System.out.println("Replicated Database : Execute Update (" + statement + ")");		
		  super.invoke("replicate", joinPoint, new Object[] { statement });
		  
	}

	@RemoteAdvice(id = "database_update")
	public void executeUpdate(RemoteJoinPoint rjp, String statement)  {		
		System.out.println("Replicated Database : Received Update (" + statement + ")");
	}

}