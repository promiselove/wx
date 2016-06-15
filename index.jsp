<%@ page import="com.weixin.WeiXinService" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.io.File" %><%--
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
/*System.out.println(WeiXinService.addMaterialEver(application,new File("C:/Users/Amadeus/Desktop/material/aa.png"),"image"));*/
/*System.out.println(WeiXinService.addMaterialEver(application,new File("C:/Users/Amadeus/Desktop/material/bb.mp3"),"voice"));*/
//System.out.println(WeiXinService.addMaterialEver(application,new File("C:/Users/Amadeus/Desktop/material/cc.mp4"),"video"));
%>
<html>
  <head>
    <title>welcome</title>
  </head>
  <body>
     Welcome!!!!! <h1 style="text-align: center;"><%=from %></h1>
  </body>
</html>
