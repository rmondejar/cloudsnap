package org.objectweb.snap;

import damon.core.*;

import java.io.*;
import java.util.*;
import java.net.*;

import javax.servlet.http.*;
import javax.servlet.*;

public class StartupServlet extends HttpServlet {
	
  public static String bootstrap = null;
  public static Application snapApp = null;

  public void init (ServletConfig config) throws ServletException {
    super.init (config);
    ServletContext ctx = config.getServletContext();

    try {
        // ----- ATTEMPT TO LOAD LOCAL HOSTNAME -----
        String serverName = InetAddress.getLocalHost().getHostName();
        ctx.setAttribute ("serverName", serverName);        
        Properties snapwarxml = Application.getSnapAppConfig (ctx.getRealPath (File.separator));

        //String xmlConfigFile = ctx.getRealPath (File.separator) + File.separator + "WEB-INF" + File.separator + "damon-config.xml";
        String xmlConfigFile = "damon-config.xml";
        if (bootstrap!=null && bootstrap.length()>0) {
        	DamonCore.init(bootstrap, xmlConfigFile);        
        }
        else {
        	DamonCore.init(xmlConfigFile);     
        }
        DamonCore.setClassLoader(ClassLoader.getSystemClassLoader());        
        
        // Create main SNAP application handler
        snapApp = new Application (serverName, snapwarxml); 
        
        
        ctx.setAttribute (Context.SNAP_MAIN_APP, snapApp);       
                        
    } catch (Exception e) {
		
      System.out.println ("------------------------------ EXCEPTION --------------------------------");
      System.out.println ("-------------------------- Initializing SNAP ----------------------------");
      System.out.println ("-------------------------------------------------------------------------");
      e.printStackTrace();
      throw new ServletException (e.getMessage());
    }
  }

  public void destroy() {
    super.destroy();

    if (snapApp != null) {
      try {        
        snapApp.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
