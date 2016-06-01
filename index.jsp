<%@ page import="com.weixin.WeiXinService" %><%--
  Created by IntelliJ IDEA.
  User: Amadeus
  Date: 2016/5/31
  Time: 8:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" session="false" isErrorPage="true" %>
<%
  boolean isSuccess = false;
  isSuccess = WeiXinService.createMenu(application);
%>
<html>
  <head>
    <title>welcome</title>
  </head>
  <body>
     Welcome!!!!!
  </body>
</html>
