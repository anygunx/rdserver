package com.rd.net;

import com.rd.define.GameDefine;
import com.rd.net.message.HttpMessageHandler;
import com.rd.net.message.MessageSSLEngine;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.jboss.netty.handler.ssl.SslHandler;

public class ServerPipelineHttpFactory implements ChannelPipelineFactory {

    /**
     * OrderedMemoryAwareThreadPoolExecutor 保证同一Channel中处理的事件流的顺序性。
     * 但它并不保证同一Channel中的事件都在一个线程中执行。
     * 短连，目前没卵用。。
     */
    static ExecutionHandler executionHandler = new ExecutionHandler(
            new OrderedMemoryAwareThreadPoolExecutor(64, 1048576, 1048576));

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        if (GameDefine.PROTOCOL.equals(GameDefine.PROTOCOL_HTTPS)) {
            pipeline.addFirst("ssl", new SslHandler(MessageSSLEngine.getSSLEngine())); //务必放在第一位
        }
        pipeline.addLast("decoder1", new HttpRequestDecoder());
//		pipeline.addLast("decoder", new ObjectDecoder());
//		pipeline.addLast("encoder", new ObjectEncoder());
        pipeline.addLast("encoder1", new HttpResponseEncoder());
//		pipeline.addLast("ex", executionHandler);	 // 到这里worker释放了 但是channel没有
//		pipeline.addLast("handler", new MessageHandler());
//		pipeline.addLast("aggregator", new HttpChunkAggregator(20000));//暂时设置为20000 应该自己设置Chunk
//		pipeline.addLast("streamer", new ChunkedWriteHandler()); 
        pipeline.addLast("deflater", new HttpContentCompressor());
        pipeline.addLast("handler1", new HttpMessageHandler()); // 用自己的线程池了
        return pipeline;
//		return Channels.pipeline(new ObjectDecoder(),new ObjectEncoder(),executionHandler,new MessageHandler());
    }

//	public boolean excuteChunk(ChannelHandlerContext ctx, MessageEvent e)
//		      throws TooLongFrameException {
//		    // HttpMessage currentMessage = e.getMessage();
//
//		    if (e.getMessage() instanceof HttpMessage) {
//		      HttpMessage m = (HttpMessage) e.getMessage();
//		      if (m.isChunked()) {
//		        // A chunked message - remove 'Transfer-Encoding' header,
//		        // initialize the cumulative buffer, and wait for incoming
//		        // chunks.
//		        List<String> encodings = m
//		            .getHeaders(HttpHeaders.Names.TRANSFER_ENCODING);
//		        encodings.remove(HttpHeaders.Values.CHUNKED);
//		        if (encodings.isEmpty()) {
//		          m.removeHeader(HttpHeaders.Names.TRANSFER_ENCODING);
//		        }
//		        m.setContent(ChannelBuffers.dynamicBuffer(e.getChannel()
//		            .getConfig().getBufferFactory()));
//		        this.currentMessage = m;
//		      } else {
//		        // Not a chunked message - pass through.
//		        this.currentMessage = null;
//		      }
//		      return false;
//		    } else if (e.getMessage() instanceof HttpChunk) {
//		      // Sanity check
//		      if (currentMessage == null) {
//		        throw new IllegalStateException("received "
//		            + HttpChunk.class.getSimpleName() + " without "
//		            + HttpMessage.class.getSimpleName());
//		      }
//
//		      // Merge the received chunk into the content of the current message.
//		      HttpChunk chunk = (HttpChunk) e.getMessage();
//		      ChannelBuffer content = currentMessage.getContent();
//
//		      if (content.readableBytes() > maxContentLength
//		          - chunk.getContent().readableBytes()) {
//		        throw new TooLongFrameException("HTTP content length exceeded "
//		            + maxContentLength + " bytes.");
//		      }
//
//		      content.writeBytes(chunk.getContent());
//		      if (chunk.isLast()) {
//		        this.currentMessage = null;
//		        currentMessage.setHeader(HttpHeaders.Names.CONTENT_LENGTH,
//		            String.valueOf(content.readableBytes()));
//		        return true;
//		        // Channels.fireMessageReceived(ctx, currentMessage,
//		        // e.getRemoteAddress());
//		      }
//		    }
//		    return true;
//		  }
}
