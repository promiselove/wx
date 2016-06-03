<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ page import="com.weixin.WeiXinService"%>
<%@ page import="com.weixin.WeiXinUtil"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.net.URLEncoder" %>
<%
boolean isWeiXinBroswer = WeiXinUtil.isBroswerFromWeixin(request);
if(isWeiXinBroswer){//微信浏览器
    String getOpenId = "";
    String code =request.getParameter("code");
    if(code == null){
        try{
            StringBuffer redirectUrl = new StringBuffer();
            redirectUrl.append("https://open.weixin.qq.com/connect/oauth2/authorize");
            redirectUrl.append("?appid=").append(WeiXinService.APP_ID);
            redirectUrl.append("&redirect_uri=").append("http://test.365yunshang.com/wx/check.jsp");
            redirectUrl.append("&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
            response.sendRedirect(redirectUrl.toString());
            return;
        }catch(Exception e){
            e.printStackTrace();
        }
    }else{
        //获取open_id
        Map openIdResMap=getAccessAuthToken(WeiXinService.APP_ID,WeiXinService.APP_SECRET,code);
        getOpenId=openIdResMap.get("openid")+"";//openId
        Map userInfos = new HashMap();
        //此处先判断是否为关注用户，再拉取用户信息，否则会出错。
        userInfos = WeiXinService.getWeiXinUserInfos(application,getOpenId);//微信用户信息
        response.sendRedirect("/wx/index.jsp?from=微信登录");
        return;
    }
}else{//非微信浏览器
    String code = request.getParameter("code");
    String getOpenId = "";
    if(code == null){
        StringBuffer sb = new StringBuffer();
        sb.append("https://open.weixin.qq.com/connect/qrconnect");
        sb.append("?appid=").append(WeiXinService.APP_ID_OPEN);
        sb.append("&redirect_uri=").append(URLEncoder.encode("http://test.365yunshang.com/wx/check.jsp"));
        sb.append("&response_type=code&scope=snsapi_login&state=123#wechat_redirect");
        response.sendRedirect(sb.toString());
        return;
    }else{
        //获取open_id
        Map openIdResMap=getAccessAuthToken(WeiXinService.APP_ID_OPEN,WeiXinService.APP_SECRET_OPEN,code);
        getOpenId=openIdResMap.get("openid")+"";//openId
        String accessToken = openIdResMap.get("access_token")+"";//accessToken
        Map userInfos = getWeiXinUserInfosByOpen(accessToken,getOpenId);
        response.sendRedirect("/wx/index.jsp?from=pc登录");
        return;
    }
}
%>
<%!
    /***
     * 获取微信OPEN ID
     * @param appId
     * @param appSecret
     * @param code
     * @return
     * @throws IOException
     * "access_token":"ACCESS_TOKEN","expires_in":7200,"refresh_token":"REFRESH_TOKEN","openid":"OPENID","scope"
     */
    public static Map getAccessAuthToken(String appId,String appSecret,String code) throws IOException {
        StringBuffer url = new StringBuffer();
        url.append("https://api.weixin.qq.com/sns/oauth2/access_token?");
        url.append("appid=").append(appId);
        url.append("&secret=").append(appSecret);
        url.append("&code=").append(code);
        url.append("&grant_type=authorization_code");
	
        Map resultMap = null;
        try{
        	String openIdResult = WeiXinUtil.sendGet(url.toString());
            resultMap = WeiXinUtil.convertObject(openIdResult);
        }catch (Exception e){
        	e.printStackTrace();
            throw  new RuntimeException(e);
        }
        return resultMap;
    }

    /**
     * 开放平台获取微信用户信息
     * @param accessToken accessToken
     * @param f_wx_openid 用户openId
     * @return
     */
    public static Map getWeiXinUserInfosByOpen(String accessToken,String f_wx_openid){
        //获取用户信息
        String getUserInfosUrl = "https://api.weixin.qq.com/sns/userinfo?access_token="+accessToken+"&openid="+f_wx_openid+"&lang=zh_CN";
        String userWeiXinInfos = WeiXinUtil.sendGet(getUserInfosUrl);
        //用户信息Map
        Map userInfosMap = new HashMap();
        try {
            userInfosMap = WeiXinUtil.convertObject(userWeiXinInfos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfosMap;
    }
%>
