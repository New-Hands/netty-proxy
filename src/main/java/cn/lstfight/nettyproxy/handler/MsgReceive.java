package cn.lstfight.nettyproxy.handler;

import cn.lstfight.nettyproxy.util.Util;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

/**
 * <p>客户请求处理handler</p>
 *
 * @author 李尚庭
 * @date 2019-1-29
 */
public class MsgReceive extends SimpleChannelInboundHandler<HttpObject> {


    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            //http请求
            HttpRequest request = (HttpRequest) msg;
            //去掉协议解析配置逻辑
            ChannelPipeline pipeline = ctx.pipeline();
            pipeline.remove("codec");
            pipeline.remove("receive");

            Object msgData = msg;
            //与真实主机建立连接
            String[] hostAndIp = Util.getHostAndIp(request);
            if (null == hostAndIp) {
                ReferenceCountUtil.release(msg);
                ctx.close();
                return;
            }
            if (request.method().equals(HttpMethod.CONNECT)) {
                System.out.println("connect:"+hostAndIp[0]+";"+request.uri());
                //连接目标主机
                ChannelFuture future = proxyConnect(hostAndIp[0], Integer.parseInt(hostAndIp[1]), ctx, msgData);
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            System.out.println(future.channel().remoteAddress()+"连接建立失败！");
                            ctx.close();
                            future.channel().close();
                            return;
                        }
                        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                        //http数据转流数据
                        EmbeddedChannel em = new EmbeddedChannel(new HttpRequestEncoder());
                        em.writeOutbound(response);
                        Object data = em.readOutbound();
                        ctx.writeAndFlush(data).addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if (!future.isSuccess()) {
                                    ctx.close();
                                    future.cancel(true);
                                }
                            }
                        });

                        //改变处理器
                        ctx.pipeline().addLast(new MsgTransmit(future.channel()));
                    }
                });
            }else {
                System.out.println(request.method()+":"+hostAndIp[0]+";"+request.uri());
                //http数据转换为流数据
                EmbeddedChannel em = new EmbeddedChannel(new HttpRequestEncoder());
                em.writeOutbound(request);
                final Object data = em.readOutbound();
                //建立客户-服务器 桥梁
                ChannelFuture future = proxyConnect(hostAndIp[0], Integer.parseInt(hostAndIp[1]), ctx, msgData);
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            System.out.println(hostAndIp[0]+"连接建立失败！");
                            ctx.close();
                            future.channel().close();
                            return;
                        }

                        future.channel().writeAndFlush(data).addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if (!future.isSuccess()) {
                                    future.channel().close();
                                }
                            }
                        });

                        //改变处理器
                        ctx.pipeline().addLast(new MsgTransmit(future.channel()));
                    }
                });
            }
        } else {
            //不处理其他类型请求
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 发送代理请求
     *
     * @param host 主机
     * @param port 端口
     * @param ctx  当前连接上下文
     * @param msg  数据
     * @return
     */
    private ChannelFuture proxyConnect(String host, int port, final ChannelHandlerContext ctx, final Object msg) {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(ctx.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .remoteAddress(host, port)
                //置入客户连接
                .handler(new MsgTransmit(ctx.channel()))
                .option(ChannelOption.AUTO_READ, true);
        ChannelFuture connect = bootstrap.connect();

        return connect;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
