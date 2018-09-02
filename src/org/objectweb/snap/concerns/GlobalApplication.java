package org.objectweb.snap.concerns;

import java.io.Serializable;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;

import org.objectweb.snap.Context;

import damon.annotation.Abstractions;
import damon.annotation.DistributedAspect;
import damon.annotation.SourceHook;
import damon.annotation.Type;
import damon.invokation.aspectwerkz.AspectRemoting;
import damon.reflection.thisEndPoint;
import easypastry.dht.DHTException;
import easypastry.dht.DHTHandler;

/**
 * Application (ServletContext) interception for SNAP
 * 
 * @author Rubén Mondéjar <ruben.mondejar@urv.cat>
 */
@DistributedAspect(abstraction = Abstractions.LOCAL, target = Context.SNAP_URL_BASE)
public class GlobalApplication extends AspectRemoting {

	@SourceHook(source = "org.objectweb.snap.concerns.SourceHooks", method = "setAttribute", type = Type.AFTER)
	public void setAttribute(JoinPoint joinPoint) throws Throwable {

		Object[] params = thisEndPoint.getParams(joinPoint);
		DHTHandler dht = thisEndPoint.getPersistenceHandler(context);
		String key = (String) params[0];
		System.out.println("|||-> application.setAttribute(" + key + ")");
		Serializable value = (Serializable) params[1];
		if (condition(key)) dht.put(key, value);

	}

	@SourceHook(source = "org.objectweb.snap.concerns.SourceHooks", method = "getAttribute", type = Type.AROUND)
	public Object getAttribute(JoinPoint joinPoint) throws Throwable {

				
		Object value = joinPoint.proceed();
		Object[] params = thisEndPoint.getParams(joinPoint);

		String key = (String) params[0];

		System.out.println("Servlet getAttribute (" + key+ ")");
		
		if (condition(key)) {
			System.out.println("|||||<- application.getAttribute(" + key + ")");

			DHTHandler dht = thisEndPoint.getPersistenceHandler(context);
			Serializable ser;
			try {
				ser = dht.get((String) params[0]);
				if (ser != null)
					return ser;
			} catch (DHTException e) {
				e.printStackTrace();
			}
		}

		return value;
	}

	private boolean condition(String key) {		
		return key.indexOf('.') < 0 && !key.startsWith("Snap");
	}

}
