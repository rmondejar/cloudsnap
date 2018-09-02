package org.objectweb.snap;

import java.io.*;
import java.util.*;
import java.net.*;

import javax.servlet.http.*;
import javax.servlet.*;

import damon.core.DamonCore;

public class SnapAppStartup extends HttpServlet {
	
  private Application snapApp = null;  
  //private String realPath;
   
  public void init (ServletConfig config) throws ServletException {
    super.init (config);
    ServletContext ctx = config.getServletContext();
    String path = ctx.getRealPath (File.separator);
	
    try {
        // ----- ATTEMPT TO LOAD LOCAL HOSTNAME -----
        String serverName = InetAddress.getLocalHost().getHostName ();
        ctx.setAttribute ("serverName", serverName);
        //System.out.println("Context Path : "+ctx.getRealPath (File.separator));
        Properties snapwarxml = Application.getSnapAppConfig (ctx.getRealPath (File.separator));
                       
        DamonCore.setClassLoader(Thread.currentThread().getContextClassLoader());
        // Create main Snap application handler
        snapApp = new Application (serverName, snapwarxml);
        ctx.setAttribute (Context.SNAP_APP, snapApp);        
        
        
    } catch (Exception e) {
      System.out.println ("------------------------------ EXCEPTION --------------------------------");
      System.out.println ("------------------------ Initializing Snap App --------------------------");
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
