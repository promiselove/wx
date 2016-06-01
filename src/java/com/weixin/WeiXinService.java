package com.weixin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.servlet.ServletContext;
import org.dom4j.DocumentException;

public class WeiXinService {

	public static final String APP_ID="wx91bb7ecbb79282d1";
	public static final String APP_SECRET="d07b1ef6c3868dbc98e12e3f675574d6";
	public static final String APP_WX_ACCOUNT="gh_d891f94b50e9";
	public static final String MCH_ID="";
	public static final String PARTNER_ID="";
	public static final String PARTNER_KEY="";
    /**
     * 与接口配置信息中的 token 要一致，这里赋予什么值，在接口配置信息中的Token就要填写什么值，
     * 两边保持一致即可，建议用项目名称、公司名称缩写等，我在这里用的是项目名称weixinface
     */
    private static String token = "amadeuswxtest";

	/**
	 * 从application中获取accessToken
	 * @param application
	 * @return
	 */
	public static String getAccessAuthToken(ServletContext application){
        Object saveTimeObject = application.getAttribute("accessTokenTime");
        if (saveTimeObject == null) {
            synchronized(WeiXinService.class){
                if(application.getAttribute("accessTokenTime") == null){
                    application.setAttribute("accessToken",getAccessAuthToken(APP_ID,APP_SECRET).get("access_token"));
                    Date date = new Date();
                    application.setAttribute("accessTokenTime", date.getTime()); 
                }
            }
        } else {
            Date date = new Date();
            long endTime = date.getTime();
            long changeTime = endTime - Long.parseLong(saveTimeObject+"");
            if (changeTime > 5400*1000) {
                synchronized(WeiXinService.class){
                    changeTime = new Date().getTime() -  Long.parseLong(application.getAttribute("accessTokenTime")+"");
                    if(changeTime > 5400*1000){
                        application.setAttribute("accessToken",getAccessAuthToken(APP_ID,APP_SECRET).get("access_token"));
                        Date startDate = new Date();
                        application.setAttribute("accessTokenTime", date.getTime());
                    }
                }
            }
        }
        return application.getAttribute("accessToken")+"";
    }
	
	/**
	 * 获取accessToken
	 * @param appId
	 * @param appSecret
	 * @return
	 */
	private static Map getAccessAuthToken(String appId,String appSecret){
        StringBuffer url = new StringBuffer();
        url.append("https://api.weixin.qq.com/cgi-bin/token?");
        url.append("appid=").append(appId);
        url.append("&secret=").append(appSecret);
        url.append("&grant_type=client_credential");
        Map resultMap = null;
        try{
        	String result = WeiXinUtil.sendGet(url.toString());
            resultMap = WeiXinUtil.convertObject(result);
        }catch (Exception e){
        	e.printStackTrace();
            throw  new RuntimeException(e);
        }
        return resultMap;
    }

	public static void main(String args[]){
		
	}
	
	/**
	 * 获取微信用户信息
	 * @param application
	 * @param f_wx_openid
	 * @return
	 */
	public static Map getWeiXinUserInfos(ServletContext application,String f_wx_openid){
		String accessToken = getAccessAuthToken(application);
        //获取用户信息
        String getUserInfosUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+accessToken+"&openid="+f_wx_openid+"&lang=zh_CN";
        String userWeiXinInfos = WeiXinUtil.sendGet(getUserInfosUrl);
        //用户信息Map
        Map userInfosMap = new HashMap();
    	try {
            userInfosMap = WeiXinUtil.convertObject(userWeiXinInfos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userInfosMap;
	}
	
	/**
	 * 统一下单
	 * @param body
	 * @param attach
	 * @param tradeNo
	 * @param totalFee
	 * @param spbillCreateIp
	 * @param notifyUrl
	 * @param openid
	 * @return
	 */
    public static String getPrePayOrderIdForJsApi(String body, String attach, String tradeNo,String totalFee, String spbillCreateIp, String notifyUrl, String openid){
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("appid", WeiXinService.APP_ID);
        params.put("mch_id", WeiXinService.MCH_ID);
        // 随机字符串，不长于32 位
        try
        {
            params.put("nonce_str", WeiXinUtil.getMD5(String.valueOf(new Random().nextInt(10000))));
        }
        catch (Exception e)
        {
        	throw  new RuntimeException(e);
        }
        params.put("body", body);
        params.put("attach", attach);
        params.put("out_trade_no", tradeNo);
        params.put("total_fee", totalFee);
        params.put("spbill_create_ip", spbillCreateIp);
        params.put("notify_url", notifyUrl);
        // 交易类型
        params.put("trade_type", "JSAPI");
        params.put("openid", openid);
        params.put("sign", WeiXinUtil.createSign(params, WeiXinService.PARTNER_KEY));
        String postStr = WeiXinUtil.parseXML(params);
        
        String responseParams = WeiXinUtil.sendPost("https://api.mch.weixin.qq.com/pay/unifiedorder",postStr);
        Map resultMap=new HashMap();
        //System.out.println(responseParams);
        if (responseParams == null || WeiXinUtil.isBlank(responseParams))
        {
            return null;
        }
        try {
        	resultMap=WeiXinUtil.doXMLParse(responseParams);
		} catch (DocumentException e) {
			e.printStackTrace();
			throw  new RuntimeException(e);
		}
		
		String return_code=resultMap.get("return_code").toString();
		String result_code=resultMap.get("return_code").toString();
		if ("SUCCESS".equals(return_code))
        {
        	
            if ("SUCCESS".equals(result_code))
            {
            	 
                return resultMap.get("prepay_id").toString();
            }
            else
            {
                throw new RuntimeException(resultMap.get("err_code_des").toString());
            }
        }
        else
        {
            throw new RuntimeException(resultMap.get("return_msg").toString());
        }
    }
    public static String getWeiXinBarcode(String product_id){
		String nonce_str = "";
		try {
			nonce_str = WeiXinUtil.getMD5(String.valueOf(new Random().nextInt(10000)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String time_stamp = String.valueOf(System.currentTimeMillis() / 1000);
		
		SortedMap<String, String> signParams = new TreeMap<String, String>();
		signParams.put("appId", WeiXinService.APP_ID);
		signParams.put("mch_id", WeiXinService.MCH_ID);
		signParams.put("nonceStr", nonce_str);
		signParams.put("timeStamp", time_stamp);
		signParams.put("product_id", product_id);
		String sign = WeiXinUtil.createSign(signParams, WeiXinService.PARTNER_KEY);
		
		StringBuffer url = new StringBuffer();
		
		url.append("weixin://wxpay/bizpayurl");
		url.append("?appid=" + WeiXinService.APP_ID);
		url.append("&mch_id=" + WeiXinService.MCH_ID);
		url.append("&nonce_str=" + nonce_str);
		url.append("&time_stamp=" + time_stamp);
		url.append("&product_id=" + product_id);
		url.append("&sign=" + sign);
		
		return url.toString();
	}

    /**
     * 发送微信模板消息
     * @param msgContent 消息内容
     * @param
     * @param application
     * @return {"errcode":0,"errmsg":"ok","msgid":xxxx}
       
     */
    private static Map sendTempMsg(String msgContent,ServletContext application){
        Map returnResult = new HashMap();
        try {
            String accessToken=WeiXinService.getAccessAuthToken(application);
            String requestUrl="https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessToken;
            String result = WeiXinUtil.sendPost(requestUrl,msgContent);
            returnResult = WeiXinUtil.convertObject(result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnResult;
    }
    /**
     * 模板消息数据
     * @param msgMap {"openId":"用户openid","url":点击url,"first":"头部","keynote1":"工单情况","keynote2":"申请日期","remark":"标记"}
     * @param application
     * @return 成功返回{"errcode":0,"errmsg":"ok","msgid":xxxx}
     */
    public static Map sendTempMsg(Map msgMap,ServletContext application){
    	Map temp = new HashMap();//总数据
        temp.put("touser", msgMap.get("openId"));
        temp.put("template_id","RqzXgq9mWC5JnUNrNpG7Zo_sYicI1XEe2hhPpo1adwk" );
        temp.put("url", msgMap.get("url"));
        temp.put("topcolor", "#5CACEE");
        
        //头部
        Map dataMap = new HashMap();
        Map itemMap = new HashMap();
        itemMap.put("value", msgMap.get("first"));
        itemMap.put("color", "#5CACEE");
        dataMap.put("first", itemMap);
        //工单情况
        itemMap = new HashMap();
        itemMap.put("value", msgMap.get("keynote1"));
        itemMap.put("color", "#5CACEE");
        dataMap.put("keynote1", itemMap);
        //申请日期
        itemMap = new HashMap();
        itemMap.put("value", msgMap.get("keynote2"));
        itemMap.put("color", "#5CACEE");
        dataMap.put("keynote2", itemMap);
        //标记
        itemMap = new HashMap();
        itemMap.put("value", msgMap.get("remark"));
        itemMap.put("color", "#5CACEE");
        dataMap.put("remark", itemMap);
       
        temp.put("data", dataMap);
        String dataStr = WeiXinUtil.convertString(temp);
    	return sendTempMsg(dataStr,application);
    }

    /**
     * 验证签名
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce){
        String[] arr = new String[]{token, timestamp, nonce};
        // 将 token, timestamp, nonce 三个参数进行字典排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for(int i = 0; i < arr.length; i++){
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行 shal 加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        content = null;
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()): false;
    }

    /**
     * 将字节数组转换为十六进制字符串
     * @param digest
     * @return
     */
    private static String byteToStr(byte[] digest) {
        // TODO Auto-generated method stub
        String strDigest = "";
        for(int i = 0; i < digest.length; i++){
            strDigest += byteToHexStr(digest[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     * @param b
     * @return
     */
    private static String byteToHexStr(byte b) {
        // TODO Auto-generated method stub
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(b >>> 4) & 0X0F];
        tempArr[1] = Digit[b & 0X0F];

        String s = new String(tempArr);
        return s;
    }

    /**
     * 创建自定义菜单
     * @param application
     * @return
     */
    public static boolean createMenu(ServletContext application){
        String accessToken = getAccessAuthToken(application);
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+accessToken;
        Map item = new HashMap();//总数据
        List button = new ArrayList();
        //菜单1
        Map item1 = new HashMap();
        item1.put("type","click");
        item1.put("name","今日歌曲");
        item1.put("key","V1001_TODAY_MUSIC");
        //菜单2
        Map item2 =  new HashMap();
        item2.put("name","菜单");
        List subButton =  new ArrayList();//菜单2子项
        //菜单2-1
        Map item2_1 = new HashMap();
        item2_1.put("type","view");
        item2_1.put("name","搜索");
        item2_1.put("url","http://www.baidu.com");
        //菜单2-2
        Map item2_2 = new HashMap();
        item2_2.put("type","view");
        item2_2.put("name","视频");
        item2_2.put("url","http://www.qq.com");
        //菜单2-3
        Map item2_3 = new HashMap();
        item2_3.put("type","click");
        item2_3.put("name","赞一下");
        item2_3.put("key","V1001_GOOD");

        subButton.add(item2_1);
        subButton.add(item2_2);
        subButton.add(item2_3);
        item2.put("sub_button",subButton);
        //菜单3
        Map item3 = new HashMap();
        item3.put("name","呵呵");
        List subButton1 =  new ArrayList();//菜单3子项
        //菜单3-1
        Map item3_1 = new HashMap();
        item3_1.put("type","view");
        item3_1.put("name","搜索");
        item3_1.put("url","http://www.baidu.com");
        //菜单3-2
        Map item3_2 = new HashMap();
        item3_2.put("type","view");
        item3_2.put("name","视频");
        item3_2.put("url","http://www.qq.com");
        //菜单3-3
        Map item3_3 = new HashMap();
        item3_3.put("type","click");
        item3_3.put("name","赞一下");
        item3_3.put("key","V3001_GOOD");
        //菜单3-4
        Map item3_4 = new HashMap();
        item3_4.put("type","click");
        item3_4.put("name","赞二下");
        item3_4.put("key","V3002_GOOD");
        //菜单3-5
        Map item3_5 = new HashMap();
        item3_5.put("type","click");
        item3_5.put("name","赞三下");
        item3_5.put("key","V3003_GOOD");

        subButton1.add(item3_1);
        subButton1.add(item3_2);
        subButton1.add(item3_3);
        subButton1.add(item3_4);
        subButton1.add(item3_5);
        item3.put("sub_button",subButton1);

        //总按钮
        button.add(item1);
        button.add(item2);
        button.add(item3);
        item.put("button",button);
        String datas = WeiXinUtil.convertString(item);
        Map isCreate = new HashMap();
        isCreate = WeiXinUtil.convertObject(WeiXinUtil.sendPost(url,datas));
        return "0.0".equals(isCreate.get("errcode")+"");
    }

    /**
     * 获取自动回复规则
     * @param application
     * @return
     */
    public static String getAutoRule(ServletContext application){
        String accessToken = getAccessAuthToken(application);
        String url = "https://api.weixin.qq.com/cgi-bin/get_current_autoreply_info?access_token="+accessToken;
        return WeiXinUtil.sendGet(url);
    }

    /**
     * 回复文本消息
     * @param ToUserName 用户openId
     * @param content 回复内容
     * @return
     */
    public static String sendText(String ToUserName,String content){
        long time = new Date().getTime();
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        sb.append("<ToUserName><![CDATA["+ToUserName+"]]></ToUserName>");
        sb.append("<FromUserName><![CDATA["+WeiXinService.APP_WX_ACCOUNT+"]]></FromUserName>");
        sb.append("<CreateTime><![CDATA["+time+"]]></CreateTime>");
        sb.append("<MsgType><![CDATA[text]]></MsgType>");
        sb.append("<Content><![CDATA["+content+"]]></Content>");
        sb.append("</xml>");
        return sb.toString();
    }
}

