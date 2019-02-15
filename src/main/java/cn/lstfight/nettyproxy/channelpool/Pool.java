package cn.lstfight.nettyproxy.channelpool;

import io.netty.channel.Channel;

/**
 * <p>在本应用层的复用连接 或者在netty层就不及时关闭</p>
 *
 * @author 李尚庭
 * @date 2019-2-14
 */
public interface Pool {

    Channel getChannelByIp(String ip);

    void putChannel(String ip, Channel channel);

}
