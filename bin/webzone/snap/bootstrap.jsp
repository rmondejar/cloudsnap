<%@page errorPage="exceptionHandler.jsp"
        import="dermi.*, dermi.registry.*, org.objectweb.snap.*,
                java.util.*, java.io.*, java.util.logging.*, java.net.*, javax.naming.*"
%>

<%
  response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
  response.setHeader("Pragma","no-cache"); //HTTP 1.0
  response.setDateHeader ("Expires", 0); //prevents caching at the proxy server

  String[] jsessionid = (String[]) request.getAttribute("_jsessionid");
  String[] url = (String[]) request.getAttribute("_url");
  	
  //swap jsessionid's
  if (jsessionid[0]!=null) {	
  
    //DHTHandler or PersistenceHandler ?
    
    //Object session = DHTHandler.remove(jsessionid[0]);
    //String new_jsessionid = request.getSession().getId();
    //DHTHandler.insert(new_jsessionid, session);
    
  }
  response.sendRedirect (url[0]);

%>
