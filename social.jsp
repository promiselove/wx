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
try {
    if("wx_mp".equals(action)){//接入公众号配置
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        boolean isFromWx = WeiXinService.checkSignature(signature,timestamp,nonce);
        if(isFromWx){
            //消息类型
            if("event".equals(requestDaas.get("MsgType"))){//事件类型
                if("subscribe".equals(requestDaas.get("Event"))){//关注事件
                    String textData = WeiXinService.sendText(requestDaas.get("FromUserName"),"欢迎关注Amadeus的测试微信号！");
                    response.getWriter().write(textData);
                }else if("unsubscribe".equals(requestDaas.get("Event"))){//取消关注

                }
            }
            response.getWriter().write(echostr);
        }
    }
}catch (Exception e){
    result.put("isSuccess",false);
    result.put("msg",e.getMessage());
}finally {

}
%>
