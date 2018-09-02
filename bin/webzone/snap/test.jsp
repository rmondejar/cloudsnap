<%@page import="dermi.*, dermi.core.*, dermi.registry.*, org.objectweb.snap.*,
		java.util.*, java.io.*, java.util.logging.*, java.net.*, javax.naming.*"
%>

<%
  response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
  response.setHeader("Pragma","no-cache"); //HTTP 1.0
  response.setDateHeader ("Expires", 0); //prevents caching at the proxy server

  Hashtable contexts = Registry.getStorageInfo();
  Iterator it_contexts = contexts.keySet().iterator();
  out.println("<h1>Storage Contexts</h1>");
  while(it_contexts.hasNext()) {
    String context_name = (String)it_contexts.next();
    out.println("<h2>"+context_name+"</h2>");

    //Hashtable cntxt_data = Registry.getStorageInfo();
    Hashtable cntxt_data = (Hashtable)contexts.get(context_name);
    Iterator it_keys = cntxt_data.keySet().iterator();
    out.println("<table border=\"1\">");
	while(it_keys.hasNext()) {
      Id key = (Id)it_keys.next();
      out.println("<tr><td>"+key+"</td><td>"+cntxt_data.get(key)+"</td></tr>");	
	}
    out.println("</table>");
  }

  contexts = Registry.getReplicaInfo();
  it_contexts = contexts.keySet().iterator();
  out.println("<h1>Replicas</h1>");
  while(it_contexts.hasNext()) {
    String context_name = (String)it_contexts.next();
    out.println("<h2>"+context_name+"</h2>");

    //Hashtable cntxt_data = Registry.getStorageInfo();
    Hashtable cntxt_data = (Hashtable)contexts.get(context_name);
    Iterator it_keys = cntxt_data.keySet().iterator();
    out.println("<table border=\"1\">");
	while(it_keys.hasNext()) {
      Id key = (Id)it_keys.next();
      out.println("<tr><td>"+key+"</td><td>"+cntxt_data.get(key)+"</td></tr>");	
	}
    out.println("</table>");
  }
%>
