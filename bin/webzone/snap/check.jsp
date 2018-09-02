<%@page errorPage="exceptionHandler.jsp"%>       
        
<%@ page import = "java.util.*" %>
<%@ page import = "java.io.*" %>
<%@ page import = "org.objectweb.snap.*" %>
<%@ page import = "org.objectweb.snap.deployer.*" %>
<%@ page import = "org.objectweb.snap.exception.*" %>

<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>SNAP: Structured overlay Networks Application Platform: Welcome!</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta name="keywords" content="SNAP2, SNAP, Damon, Dermi, DHT, Middleware, Framework">
<meta name="description" content="SNAP initial page">
<meta name="Author" content="Rubén Mondéjar & Carles Pairot">
<META NAME="robots" CONTENT="index, follow">
<!-- (Robot commands: All, None, Index, No Index, Follow, No Follow) -->
<META NAME="revisit-after" CONTENT="30 days">
<META NAME="distribution" CONTENT="global">
<META NAME="rating" CONTENT="general">
<META NAME="Content-Language" CONTENT="english">
<script language="JavaScript" type="text/JavaScript" src="images/functions.js"></script><link href="images/style.css" rel="stylesheet" type="text/css">
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
      <table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" background="images/topnavbg.jpg">
        <tr>
          <td height="9" colspan="2" background="images/basebg2.jpg">
            <img src="images/basebg2.jpg" width="1" height="9">
          </td>
        </tr>
        <tr>
          <td width="150" rowspan="2" valign="top">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td valign="top">
                  <img border="0" src="images/snap.jpg" width="151" height="167"></td>
              </tr>
              <tr>
                <td height="1" bgcolor="#FFFFFF">
                  <img src="images/spacer.gif" width="1" height="1"></td>
              </tr>
              <tr>
                <td>
                  &nbsp;<p>
                  <img src="images/arrow.jpg" width="20" height="10">
                  Interesting Links</td>
              </tr>
              <tr>
                <td>
                  <img src="images/spacer.gif" width="1" height="1">
                  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td width="20" rowspan="3">
                        <img src="images/spacer.gif" width="20" height="1">
                      </td>
                      <td>
                        <br>
                        <a href="http://deim.urv.cat/~pgarcia/AST/">
                        <span class="sidelinks">AST Group</span>
                      </a>
                      </td>
                    </tr>
                    <tr>
                      <td class="sidelinks2">Architecture &amp; Telematic Services
                      Research Group</td>
                    </tr>
                    <tr>
                      <td class="sidelinks2">                        &nbsp;</td>
                    </tr>
                    <tr>
                      <td width="20" rowspan="3">
                        <img src="images/spacer.gif" width="20" height="1">
                      </td>
                      <td>
                        <br>
                        <a href="http://planet.urv.es/DERMI">
                        <span class="sidelinks">Dermi</span>
                      </a>
                      </td>
                    </tr>
                    <tr>
                      <td class="sidelinks2">Decentralized Event Remote Method
                      Invocation</td>
                    </tr>
                    <tr>
                      <td class="sidelinks2">                        &nbsp;</td>
                    </tr>
                    <tr>
                      <td width="20" rowspan="3">
                        <img src="images/spacer.gif" width="20" height="1">
                      </td>
                      <td>
                        <br>
                        <a href="http://planet.urv.es/damon">
                        <span class="sidelinks">Damon</span> </a>
                      </td>
                    </tr>
                    <tr>
                      <td class="sidelinks2">Distributed p2p Aspect Middleware on top of Overlay Network</td>
                    </tr>
                    <tr>
                      <td class="sidelinks2">                        &nbsp;</td>
                    </tr>
                    <tr>
                      <td width="20" rowspan="3">
                        <img src="images/spacer.gif" width="20" height="1">
                      </td>
                      <td>
                        <br>
                        <a href="http://planet.urv.es/bunshin">
                        <span class="sidelinks">Bunshin</span> </a>
                      </td>
                    </tr>
                    <tr>
                      <td class="sidelinks2">DHT Replication and Caching</td>
                    </tr>
                    <tr>
                      <td class="sidelinks2">                        &nbsp;</td>
                    </tr>
                    <tr>
                      <td width="20" rowspan="3">
                        <img src="images/spacer.gif" width="20" height="1">
                      </td>
                      <td>
                        &nbsp;</td>
                    </tr>
                    <tr>
                      <td class="sidelinks2">&nbsp;</td>
                    </tr>
                    <tr>
                      <td class="sidelinks2">                        &nbsp;</td>
                    </tr>
                    <tr>
                      <td rowspan="3">
                        <img src="images/spacer.gif" width="20" height="1">
                      </td>
                      <td>
                        &nbsp;</td>
                    </tr>
                    <tr>
                      <td class="sidelinks2">&nbsp;</td>
                    </tr>
                    <tr>
                      <td class="sidelinks2">                        &nbsp;</td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </td>
          <td valign="top" bgcolor="#FFFFFF">
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td height="29" colspan="3" background="images/navbasebg.jpg">
                  <img src="images/navbasebg.jpg" width="1" height="29">
                </td>
              </tr>
              <tr>
                <td width="40">&nbsp;</td>
                <td>
                  <h1>
                    <br>
                    <%
String tmp = "storage" + File.separator + "_deployTempDirectory";
File dir = new File(tmp);
if (!dir.exists()) dir.mkdirs();

String name = Deployer.uploadFile(request,tmp);
File f = new File(tmp + File.separator + name);

Properties metadata = Deployer.extractWarInfo(tmp + File.separator + name);
String p2pUrl = metadata.getProperty (Context.SNAP_APPURL);
metadata.setProperty (Context.SNAP_WARFILE, name);

Deployer.deployWarFile(f,p2pUrl,metadata);

%>

					<p>
                    &nbsp;</p>

<p align="center"><b><font size="4">Webapp</font><font size="4"> deployed</font></b></p>
					<p align="center">&nbsp;</p>
<p align="center"><font size="4">File Name    :   <%=name%></font></p>
<p align="center"><font size="4">p2pURL   :   <%=p2pUrl%></font></p>
<p align="center"><font size="4">File Length  :   <%=(f.length()/1024)+" KB"%></font></p>

					<font size="4">

<%f.delete();%>

</font>

<p align="center">&nbsp;</p>
					<p align="center"><i><font size="3"><a href="index.jsp">Return to Snap Front Page</a></font></i></p>
					<p>&nbsp;</p>
					<p>&nbsp;</p>
					<p>&nbsp;</p></h1>
					<table width="100%" border="0" cellspacing="0" cellpadding="5" id="table1">
                    <tr>
                      <td align="center" valign="top">
                        <hr>
                      </td>
                    </tr>
                  </table>
                  <p>
			<img border="0" src="images/logodamon.jpg" width="163" height="170" align="right"></td>
                <td width="25">&nbsp;</td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
      <table width="100%" border="0" cellspacing="0" cellpadding="0" height="27" background="images/basebg2.jpg">
        <tr>
          <td height="27" class="baseline">&nbsp;&nbsp;© Copyright 2007 Rubén Mondéjar &amp; Carles Pairot. Universitat Rovira i Virgili. All Rights Reserved.</td>
        </tr>
      </table>
  </tr>
</table>
</body>
</html>
