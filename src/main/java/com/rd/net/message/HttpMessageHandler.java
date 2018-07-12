package com.rd.net.message;

import com.rd.action.GameAction;
import com.rd.define.GameDefine;
import com.rd.game.GameWorld;
import com.rd.net.web.WebAction;
import com.rd.net.web.WebActionManager;
import com.rd.util.SecurityUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;

import java.io.IOException;

public class HttpMessageHandler extends SimpleChannelUpstreamHandler {

    static Logger log = Logger.getLogger(HttpMessageHandler.class);

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelConnected(ctx, e);
        //       System.out.println("连接:"+ctx.getChannel().hashCode());
//        Logger.info(LoggerSystem.NET, "http=" + ctx.getChannel().getRemoteAddress() + "连接");
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelClosed(ctx, e);
//		System.out.println("断开:"+ctx.getChannel().hashCode());
//		Logger.info(LoggerSystem.NET, "http=" + ctx.getChannel().getRemoteAddress() + "关闭");
    }

    @Override
    public void childChannelClosed(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
//		System.out.println("断开child:"+ctx.getChannel().hashCode());
        super.childChannelClosed(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        Object msg = e.getMessage();
        Channel ch = e.getChannel();
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            if (req.getMethod() == HttpMethod.POST) {
                if (GameWorld.getPtr().isClose()) {
                    log.info("服务器已关闭，不处理任何游戏消息");
                    ch.close();
                    return;
                }
                if (HttpHeaders.is100ContinueExpected(req)) {
                    Channels.write(ctx, Channels.future(ch),
                            new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
                }

                // 接收数据
                ChannelBuffer buf = req.getContent();
                if (buf.readableBytes() < 2) {
                    //Logger.info(LoggerSystem.NET, "接收到异常命令********************：<2");
                    return;
                }
                buf.markReaderIndex();
                int len = buf.readInt();
                if (buf.readableBytes() < len) {
                    buf.resetReaderIndex();
                    return;
                }
                // 每个消息前面都要有一个玩家id，如果是登陆则这个id不进行处理
                short cmdId = buf.readShort();
                int playerId = buf.readInt();
                short loginCode = buf.readShort();

                if (!GameDefine.ISPUBLISH) {
                    if (cmdId != 99 && cmdId != 2001 && cmdId != 2002)
                        log.info("playerId= " + playerId + " receive cmdId:" + cmdId);
                }
                Message message = Message.buildReceiveMessage(ch, cmdId, buf);
                try {
                    GameAction.execute(playerId, loginCode, message);
                } catch (Exception exception) {
                    log.error("Exception caught in message received with playerId=" + playerId + " cmdId=" + cmdId);
                    exception.printStackTrace();
                    throw exception;
                }
            } else if (req.getMethod() == HttpMethod.GET) {
                handlerWebAction(req, ch);
            }
        }
    }

    /**
     * 处理HTTP请求
     */
    private void handlerWebAction(HttpRequest request, Channel channel) {
        String uri = request.getUri();
        if (!uri.contains("?"))
            return;
        //校验IP
        String address = channel.getRemoteAddress().toString();
        if (!GameDefine.ISPVP) {
            if (!SecurityUtil.ipValidate(address)) {
                log.error("HttpMessageHandler.handlerWebAction() reject " + uri + " request from " + address);
                return;
            }
        }
        //消息分发
        String filter = uri.substring(1, uri.indexOf("?"));
        WebAction action = WebActionManager.getAction(filter);
        if (action != null) {
            //JSON格式数据处理
            if (action.isJson) {
                action.doJsonRequest(request, channel);
            }
            //非JSON格式数据处理
            else {
                action.doRequest(request, channel);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        if (e.getCause().getClass() != IOException.class)
            log.error("Http异常...", e.getCause());
        e.getChannel().close();
    }
}
