<%@page errorPage="exceptionHandler.jsp"
        import="damon.core.*, org.objectweb.snap.*, java.util.*, java.io.*, java.util.logging.*, java.net.*, javax.naming.*"
%>

<%
  response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
  response.setHeader("Pragma","no-cache"); //HTTP 1.0
  response.setDateHeader ("Expires", 0); //prevents caching at the proxy server


  String p2purl = request.getParameter ("p2purl");
  Application snapSkel = (Application) application.getAttribute ("SNAPSkel");  

  if (p2purl != null && snapSkel != null) {   
  
    String snapTimer = "" + System.currentTimeMillis();
        
    String httpurl = snapSkel.resolveP2PURL(p2purl);    
    
    //INI TESTING
    //response.sendRedirect (httpurl);
    
    
    String testurl = httpurl + "index.jsp?timestamp=" + snapTimer;
    response.sendRedirect (testurl);
    
    //END TESTING

  }
  else {
    throw new NullPointerException ("Invalid parameter received in connect.jsp!");
  }

%>
