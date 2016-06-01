package com.weixin;

import java.io.*;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * 
 * @author Amadeus
 *
 */
public class WeiXinUtil {
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static Gson gson = new Gson();
	private static Gson gjson = null;
	  
	  static
	  {
	    GsonBuilder gsonBuilder = new GsonBuilder();
	    gjson = gsonBuilder.create();
	  }

    /**
     * JSON转Map
     * @param jsonStr
     * @return
     */
    public static Map convertObject(String jsonStr) {
        if(jsonStr != null) {
            Map jsonObject = (Map)gson.fromJson(jsonStr, Map.class);
            return jsonObject;
        } else {
            return null;
        }
    }

    /**
     * JSON转List
     * @param jsonStr
     * @return
     * @throws UnsupportedEncodingException
     */
    public static List convertList(String jsonStr) throws UnsupportedEncodingException {
        return (List)gson.fromJson(jsonStr, List.class);
    }

    /**
     * Objet转JSON
     * @param obj
     * @return
     */
    public static String convertString(Object obj) {
        return gjson.toJson(obj);
    }
	public static String fomatDate(Date date, String format)
	  {
	    DateFormat dateFormat = null;
	    if ((format == null) || (format.length() == 0)) {
	      format = "yyyy-MM-dd";
	    }
	    dateFormat = new SimpleDateFormat(format);
	    return dateFormat.format(date);
	  }
	
	public static boolean hasText(String str)
	  {
	    return hasText(str);
	  }
	  
	public static boolean hasText(Object object)
	  {
	    if (object == null) {
	      return false;
	    }
	    return hasText(object.toString());
	  }
	
	public static String getText(Object object)
	  {
	    if (object == null) {
	      return "";
	    }
	    return object.toString();
	  }
	 
    public static Date parseDate(String date)
	    throws Exception
	  {
	    return parseDate(date, null);
	  }
	  
	public static Date parseDate(String date, String format)
	    throws Exception
	  {
	    DateFormat dateFormat = null;
	    if ((format == null) || (format.length() == 0)) {
	      format = "yyyy-MM-dd";
	    }
	    dateFormat = new SimpleDateFormat(format);
	    return dateFormat.parse(date);
	  }
	
	public static String createSign(SortedMap<String, String> packageParams, String partnerkey)
    {
        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k))
            {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + partnerkey);
        try
        {
            String sign = getMD5((sb.toString())).toUpperCase();
            return sign;
        }
        catch (Exception e)
        {
            return null;
        }
    }

	public static String parseXML(SortedMap<String, String> packageParams)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set<Entry<String, String>> es = packageParams.entrySet();
        Iterator<Entry<String, String>> it = es.iterator();
        while (it.hasNext())
        {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"appkey".equals(k))
            {

                sb.append("<" + k + ">" + getParameter(packageParams, k) + "</" + k + ">\n");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }
    
    public static String getParameter(SortedMap<String, String> packageParams, String parameter)
    {
        String s = (String) packageParams.get(parameter);
        return (null == s) ? "" : s;
    }
    
    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url 发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection  conn = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("contentType", "UTF-8");
            conn.setRequestMethod("POST");  
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
    
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param
     *
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, Map params) {
    	StringBuffer param=new StringBuffer();
    	int size=params.size();
    	for (Object key:params.keySet()) {
    		if(size>1){
    			param.append(key+"="+params.get(key)+"&");
    		}else{
    			param.append(key+"="+params.get(key));
    		}
    		size--;
		}
    	param.toString();
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection  conn = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("contentType", "UTF-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }   
    /**
     * 获取请求ip地址
     * @param request HttpServletRequest
     * @return ip地址
     */
	public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
	
	/**
	 * MD5加密后的字符串
	 * @param str 待加密字符串
	 * @return 加密字符串
	 * @throws Exception
	 */
	public static String getMD5(String str)throws Exception {  
		MessageDigest md;  
		// 生成一个MD5加密计算摘要  
		md = MessageDigest.getInstance("MD5");  
		// 计算md5函数  
	    md.update(str.getBytes());  
	    // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符  
	    // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值  
	    String _str = new BigInteger(1, md.digest()).toString(16);   
	    return _str;  
	}
	
	/**
	 * XML转化为Map
	 * @param strxml StringXML
	 * @return Map集合
	 * @throws DocumentException
	 */
	public static Map doXMLParse(String strxml) throws DocumentException
	{
	    strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");
	    if ((strxml == null) || ("".equals(strxml))) {
	      return null;
	    }
	    Map<String, Object> m = new HashMap();
	    
	    Document doc = DocumentHelper.parseText(strxml);
	    Element root = doc.getRootElement();
	    List<Element> list = root.elements();
	    Iterator<Element> it = list.iterator();
	    while (it.hasNext())
	    {
	      Element e = (Element)it.next();
	      String k = e.getName();
	      String v = "";
	      List<Element> children = e.elements();
	      if (children.isEmpty()) {
	        v = e.getText();
	      } else {
	        v = getChildrenText(children);
	      }
	      m.put(k, v);
	    }
	    return m;
	  }
	  
	private static String getChildrenText(List<Element> children)
	{
	    StringBuffer sb = new StringBuffer();
	    if (!children.isEmpty())
	    {
	      Iterator<Element> it = children.iterator();
	      while (it.hasNext())
	      {
	        Element e = (Element)it.next();
	        String name = e.getName();
	        String value = e.getText();
	        List<Element> list = e.elements();
	        sb.append("<" + name + ">");
	        if (!list.isEmpty()) {
	          sb.append(getChildrenText(list));
	        }
	        sb.append(value);
	        sb.append("</" + name + ">");
	      }
	    }
	    return sb.toString();
	}
	  
	public static boolean isBlank(String... strs)
	{
	    String[] arrayOfString = strs;int j = strs.length;
	    for (int i = 0; i < j; i++)
	    {
	      String str = arrayOfString[i];
	      if (str == null) {
	        return true;
	      }
	      if ("".equals(str.replace(' ', ' ').trim())) {
	        return true;
	      }
	    }
	    return false;
	}

	public static boolean isEmpty(Map map)
	{
	    return (map == null) || (map.isEmpty());
	}
	  
	public static String getServerName(HttpServletRequest request)
	{
	    StringBuffer fullCtx = new StringBuffer();
	    fullCtx.append(request.getScheme()).append("://");
	    fullCtx.append(request.getServerName());
	    fullCtx.append(isBlank(new String[] { request.getContextPath() }) ? "" : request.getContextPath());
	    return fullCtx.toString();
	}
	  
	public static double stringToDouble(String str, double defaultValue)
	{
	    if (isBlank(new String[] { str }))
	    {
	        return defaultValue;
	    }

	    try
	    {
	        return Double.parseDouble(str);
	    }
	    catch (NumberFormatException nfe) {
	    }
	    return defaultValue;
	}
	
	public static boolean isBroswerFromWeixin(HttpServletRequest request){
	    if (request.getHeader("user-agent").toLowerCase().indexOf("micromessenger") != -1){
	        return true;
	    }
	    return false;
    }

    public static SortedMap<String, String> getPostXmlData(HttpServletRequest request)
    {
        SortedMap<String, String> xmlMap = new TreeMap<String, String>();
        String postStr = null;
        try
        {
            postStr = readStreamParameter(request.getInputStream());
            xmlMap.putAll(WeiXinUtil.doXMLParse(postStr));
            return xmlMap;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    //从输入流读取post参数
    private static String readStreamParameter(ServletInputStream in)
    {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != reader)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return buffer.toString();
    }
}
