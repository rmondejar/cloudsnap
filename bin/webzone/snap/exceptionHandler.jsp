<%@ page isErrorPage="true" import="java.io.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>SNAP: Exceptional event occured!</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta name="keywords" content="Keywords here">
<meta name="description" content="Description here">
<meta name="Author" content="Ruben Mondejar & Carles Pairot">
<META NAME="robots" CONTENT="index, follow"> <!-- (Robot commands: All, None, Index, No Index, Follow, No Follow) -->
<META NAME="revisit-after" CONTENT="30 days">
<META NAME="distribution" CONTENT="global">
<META NAME="rating" CONTENT="general">
<META NAME="Content-Language" CONTENT="english">
<script language="JavaScript" type="text/JavaScript" src="images/functions.js"></script><link href="images/style.css" rel="stylesheet" type="text/css"></head>

<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0" background="images/topnavbg.jpg">
        <tr>
          <td height="9" colspan="2" background="images/basebg2.jpg"><img src="images/basebg2.jpg" width="1" height="9"></td>
        </tr>
        <tr>
          <td width="5%" rowspan="2" valign="top">&nbsp;</td>
          <td valign="top" bgcolor="#FFFFFF" width="95%">
            <table width="100%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td height="29" colspan="3" background="images/navbasebg.jpg"><img src="images/navbasebg.jpg" width="1" height="29"></td>
              </tr>
              <tr>
                <td width="40">&nbsp;</td>
                <td>
                  <h1><br>
                    Snap</h1>
                  <h2>Internal exception occured.</h2>
                  <%-- Exception Handler --%>
                  <font color="red">
                    <b>
                      Unable to find application's required object in the decentralized registry.
                    <br>
                    </font>
                    <%
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    exception.printStackTrace(pw);
                    out.print(sw);
                    sw.close();
                    pw.close();
                    %>
                    </b>
                  </td>
                <td width="25">&nbsp;</td>
              </tr>
            </table>
			</td>
        </tr>
        <tr>
          <td bgcolor="#FFFFFF" width="95%">
			&nbsp;</td>
        </tr>
      </table></td>
  </tr>
  <tr>
    <td height="24" background="images/basebg1.jpg"><img src="images/basebg1.jpg" width="1" height="24"></td>
  </tr>
  <tr>
    <td height="44" valign="top" background="images/basebg2.jpg">    <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td height="26" class="baseline">&nbsp;&nbsp;© Copyright 2005 Carles Pairot. All Rights Reserved.</td>
        <td align="right" class="baseline">&nbsp;</td>
      </tr>
    </table></td>
  </tr>
</table>
</body>
</html>
