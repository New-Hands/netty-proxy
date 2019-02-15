package cn.lstfight.nettyproxy.httpproxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * <p>配置netty服务</p>
 *
 * @author 李尚庭
 * @date 2019-1-29
 */
public class ProxyServer {

    /**
     * 端口 port
     */
    private int port;

    public ProxyServer(int port) {
        this.port = port;
    }

    public void run() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        serverBootstrap.group(boosGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ProxyHandlerInitial())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 6000)
                .childOption(ChannelOption.AUTO_READ, true);
        try {
            //阻塞
            System.out.println("proxy start listening：" + port);
            ChannelFuture bindFuture = serverBootstrap.bind(port).sync();
            bindFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
