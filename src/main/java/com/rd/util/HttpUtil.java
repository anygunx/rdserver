package com.rd.util;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * HTTP操作的工具
 *
 * @author Created by U-Demon on 2016年10月26日 下午2:10:14
 * @version 1.0.0
 */
public class HttpUtil {

    private static Logger logger = Logger.getLogger(HttpUtil.class);

    /**
     * 发送HTTP回应
     *
     * @param channel
     * @param content
     */
    public static void sendResponse(Channel channel, String content) {
        try {
            ChannelBuffer buf = ChannelBuffers.wrappedBuffer(content.getBytes("utf-8"));
            sendResponse(channel, buf);
        } catch (UnsupportedEncodingException e) {
            logger.error("发生HTTP回应发生异常！", e);
        }
    }

    /**
     * 发送HTTP回应
     *
     * @param channel
     * @param content
     */
    public static void sendResponse(Channel channel, ChannelBuffer buf) {
        try {
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.setContent(buf);

            response.headers().set(Names.CONTENT_TYPE, "text/plain;charset=utf-8");
            response.headers().set(Names.CONTENT_LENGTH, response.getContent().readableBytes());
            response.headers().set(Names.CONNECTION, Values.CLOSE);
            response.headers().set(Names.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            channel.write(response);
        } catch (Exception e) {
            logger.error("发生HTTP回应发生异常！", e);
        }
    }

    /**
     * 发送GET请求
     *
     * @param url
     * @param tail
     * @param param
     */
    public static String sendHttpGet(String url, String tail, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlString = "";
            if (param != null && param.length() > 0)
                urlString = url + "?" + param + tail;
            else
                urlString = url + tail;
            URL realUrl = new URL(urlString);
            //打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(2000);
            //建立实际的连接
            conn.connect();
            //获取所有响应头字段
            //Map<String, List<String>> map = conn.getHeaderFields();
            //遍历所有响应头字段
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("发送HTTP GET请求发生异常");
        }
        //使用finally来关闭输入流
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
     * 发送GET请求
     *
     * @param url
     * @param tail
     * @param params
     * @return
     */
    public static String sendHttpGet(String url, String tail, Map<String, String> params) {
        StringBuilder param = new StringBuilder();
        for (Entry<String, String> entry : params.entrySet()) {
            param.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return sendHttpGet(url, tail, param.substring(0, param.length() - 1).toString());
    }

    /**
     * 发送POST请求
     *
     * @param url
     * @param tail
     * @param param
     * @return
     */
    public static String sendHttpPost(String url, String tail, byte[] param) {
        OutputStream out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url + tail);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = conn.getOutputStream();
            // 发送请求参数
            out.write(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("发送HTTP POST请求发生异常", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 发送POST请求
     *
     * @param url
     * @param tail
     * @param param
     * @return
     */
    public static String sendHttpPost(String url, String tail, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url + tail);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("发送HTTP POST请求发生异常", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

}
