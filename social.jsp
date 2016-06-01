<%@ page contentType="text/html;charset=UTF-8" session="false" isErrorPage="true" %>
<%@ page import="com.weixin.WeiXinService" %>
<%@ page import="com.weixin.WeiXinUtil" %>
<%@ page import="java.util.SortedMap" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%
  Map result = new HashMap();
  result.put("isSuccess",true);
  String action = request.getParameter("action");
  SortedMap<String, String> requestDaas = WeiXinUtil.getPostXmlData(request);
  System.out.println(requestDaas);
  try {
    if("wx_mp".equals(action)){
      String signature = request.getParameter("signature");
      String timestamp = request.getParameter("timestamp");
      String nonce = request.getParameter("nonce");
      String echostr = request.getParameter("echostr");
      boolean isFromWx = WeiXinService.checkSignature(signature,timestamp,nonce);
      if(isFromWx){
        response.getWriter().write(echostr);
      }
    }else if("test".equals(action)){

    }
  }catch (Exception e){
    result.put("isSuccess",false);
    result.put("msg",e.getMessage());
  }finally {

  }
%>
