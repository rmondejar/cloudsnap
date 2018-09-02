<%@page errorPage="exceptionHandler.jsp"
        import="dermi.*, dermi.registry.*, org.objectweb.snap.*,
                java.util.*, java.io.*, java.util.logging.*, java.net.*, javax.naming.*"
%>

<%
  response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
  response.setHeader("Pragma","no-cache"); //HTTP 1.0
  response.setDateHeader ("Expires", 0); //prevents caching at the proxy server

  String params = "";
  Map map = request.getParameterMap();  		
  Enumeration names = request.getParameterNames();
  String host = (String) request.getAttribute("_NEW_HOST");
  String url = (String) request.getAttribute("_DESTINATION_URL");
  
   
  if (host!=null && url!=null) {		
    while (names.hasMoreElements()){
  		String name = (String) names.nextElement();
  		String[] attr = (String[]) map.get(name);
  		params += name+'='+attr[0];
  		if (names.hasMoreElements()) params += '&';	
    } 
  
    String dest = "http://" + host + url;
    if (!params.equals("")) dest += '?' + params;
    out.println("Forwarding to "+dest);
    response.sendRedirect (dest);
    
  }
  else {
    throw new NullPointerException ("New host to redirect not found");
  }

%>
