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
            String textData = "";
            //接收消息类型
            String msgType = requestDaas.get("MsgType");
            String fromUserName = requestDaas.get("FromUserName");//用户openId
            if("event".equals(msgType)){//事件类型
                if("subscribe".equals(requestDaas.get("Event"))){//关注事件
                    textData = WeiXinService.sendText(requestDaas.get("FromUserName"),"欢迎关注Amadeus的测试微信号！");
                }else if("unsubscribe".equals(requestDaas.get("Event"))){//取消关注

                }else if("CLICK".equals(requestDaas.get("Event"))){//点击菜单根据key值回复消息
                    String eventKey = requestDaas.get("EventKey");//eventKey
                    if("V1001_TODAY_MUSIC".equals(eventKey)){
                        textData = WeiXinService.sendText(fromUserName,"今日歌曲");
                    }else if("V3001_GOOD".equals(eventKey)){
                        //textData = WeiXinService.sendText(fromUserName,"赞一下");
                        textData = WeiXinService.sendImage(fromUserName,"eTCjljIQTSI9M3XfawUtlOsUCSvFegtKxBFMLNJ2NKk");
                    }else if("V3002_GOOD".equals(eventKey)){
                        textData = WeiXinService.sendVoice(fromUserName,"eTCjljIQTSI9M3XfawUtlGBA1N5pAh2T-MQNiFvKlcc");
                    }else if("V3003_GOOD".equals(eventKey)){
                        textData = WeiXinService.sendText(fromUserName,"赞三下");
                    }else if("V1001_GOOD".equals(eventKey)){
                        textData = WeiXinService.sendText(fromUserName,"菜单赞一下");
                    }
                }else if("VIEW".equals(requestDaas.get("Event"))){//点击菜单跳转链接
                    textData = WeiXinService.sendText(fromUserName,"222222");
                }
            }else if("text".equals(msgType)){//文本消息
                textData = WeiXinService.sendText(fromUserName,"收到文本消息");
            }else if("image".equals(msgType)){//图片消息

            }else if("voice".equals(msgType)){//语音消息

            }else if("video".equals(msgType)){//视频消息

            }else if("shortvideo".equals(msgType)){//小视频消息

            }else if("location".equals(msgType)){//地理位置消息

            }else if("link".equals(msgType)){//链接消息

            }
            if("".equals(textData)){
                response.getWriter().write(echostr);
            }else{
                response.getWriter().write(textData);
            }
        }
    }
}catch (Exception e){
    result.put("isSuccess",false);
    result.put("msg",e.getMessage());
}finally {

}
%>
