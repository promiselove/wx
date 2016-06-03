<%@ page import="com.weixin.WeiXinService" %><%--
  Created by IntelliJ IDEA.
  User: Amadeus
  Date: 2016/5/31
  Time: 8:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" session="false" isErrorPage="true" %>
<%
  //boolean isSuccess = WeiXinService.createMenu(application);//创建自定义菜单
  //String result = WeiXinService.getAutoRule(application);
  //System.out.println(result);
  String from = request.getParameter("from");
  if(from == null){
    from = "未知";
  }
%>
<html>
  <head>
    <title>welcome</title>
  </head>
  <body>
     Welcome!!!!! <h1 style="text-align: center;"><%=from %></h1>
  </body>
</html>
