package cn.lstfight.nettyproxy.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * <p>数据转发handler 由传入channel 决定请求的方向</p>
 *
 * @author 李尚庭
 * @date 2019-1-29
 */
public class MsgTransmit extends ChannelInboundHandlerAdapter {

    /**
     * 客户channel 或 服务channel
     */
    private Channel outChannel;

    /**
     * 构造指定方向的channel
     *
     * @param outChannel
     */
    public MsgTransmit(Channel outChannel) {
        this.outChannel = outChannel;
    }

    /**
     * 读取消息
     * @param ctx 上下文
     * @param msg 数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("response");
        outChannel.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        outChannel.flush();
    }
}
