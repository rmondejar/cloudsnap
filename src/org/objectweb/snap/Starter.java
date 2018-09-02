//========================================================================
//Copyright 2006 Mort Bay Consulting Pty. Ltd.
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package org.objectweb.snap;

import java.net.ServerSocket;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.NCSARequestLog;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.deployer.ContextDeployer;
import org.mortbay.jetty.deployer.WebAppDeployer;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;

public class Starter {
	
	public static void resolveBootstrap(String[] args) {
		
		StartupServlet.bootstrap = "";
		for (String arg : args) {
		  if (arg.startsWith("host")) {
			  StartupServlet.bootstrap = arg.substring(5);
			  System.out.println("Arg bootstrap : "+StartupServlet.bootstrap);
		  }
		}
	}
	
	public static int resolvePort(String[] args) {
		
		int port = 8080;
		boolean finish = false;
		
    	try {
    	  if (args.length>0) {
    		  for (String arg : args) {
    			  if (arg.startsWith("port")) {
    				  String s = arg.substring(5);
    				  System.out.println("Arg port : "+s);
    				  port = Integer.parseInt(s);
    			  }
    			}
    		
    		finish = true;
    	  }	
    	}
    	catch(Exception e) {
    		finish = false;
    	}
    	
    	while(!finish) {
    		  try {
    		    ServerSocket s = new ServerSocket(port);
    		    s.close();
    		    finish = true;
    		  } catch(Exception e) {
    			System.out.println ("Snap Server Port " + (port) + " already bound. Trying " + (port+1000) + "...");
    			port+=1000;    			
    		  }
    	}    	    	
    	return port;
	}
	
    public static void main(String[] args)
        throws Exception
    {	
    	resolveBootstrap(args);
    	int port = resolvePort(args); 
    	
    	
    	Application.setServerPort(port);
      
    	String jetty_default="./webzone";
    	if (args.length>2) jetty_default = "./"+args[2];
        String jetty_home = System.getProperty("jetty.home",jetty_default);
        
        Server server = new Server();
        
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(100);
        server.setThreadPool(threadPool);
             
        Connector connector=new SelectChannelConnector();
        connector.setPort(port);
        connector.setMaxIdleTime(30000);
        server.setConnectors(new Connector[]{connector});
        
        WebAppContext snap = new WebAppContext();
        snap.setContextPath("/snap");        
        snap.setWar(jetty_home+"/snap");
        snap.setDefaultsDescriptor(jetty_home+"/etc/webdefault.xml");
        snap.setClassLoader(ClassLoader.getSystemClassLoader());        
        
        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();        
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        handlers.setHandlers(new Handler[]{snap, contexts,new DefaultHandler(),requestLogHandler});    
        
        
        server.setHandler(handlers);
        
        ContextDeployer deployer0 = new ContextDeployer();
        deployer0.setContexts(contexts);
        deployer0.setConfigurationDir(jetty_home+"/contexts");
        deployer0.setScanInterval(1);
        server.addLifeCycle(deployer0);   
        
        WebAppDeployer deployer1 = new WebAppDeployer();
        deployer1.setContexts(contexts);
        deployer1.setWebAppDir(jetty_home+"/webapps");
        deployer1.setParentLoaderPriority(false);
        deployer1.setExtract(true);
        deployer1.setAllowDuplicates(false);
        deployer1.setDefaultsDescriptor(jetty_home+"/etc/webdefault.xml");        
        server.addLifeCycle(deployer1);
                
        NCSARequestLog requestLog = new NCSARequestLog(jetty_home+"/logs/jetty-yyyy_mm_dd.log");
        requestLog.setExtended(false);
        requestLogHandler.setRequestLog(requestLog);
        
        server.setStopAtShutdown(true);
        server.setSendServerVersion(true);
        
        server.start();
        server.join();      
                
        //server.stop();
    }
    
}
