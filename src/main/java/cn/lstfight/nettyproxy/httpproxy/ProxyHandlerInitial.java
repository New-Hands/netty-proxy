package cn.lstfight.nettyproxy.httpproxy;

import cn.lstfight.nettyproxy.handler.MsgReceive;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * <p>配置initializer</p>
 *
 * @author 李尚庭
 * @date 2019-1-29
 */
public class ProxyHandlerInitial extends ChannelInitializer {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //解码http请求
        pipeline.addLast("codec",new HttpServerCodec());
        //处理客户请求
        pipeline.addLast("receive",new MsgReceive());
    }
}
