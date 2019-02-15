package cn.lstfight.nettyproxy.channelpool;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>channel容器</p>
 *
 * @author 李尚庭
 * @date 2019-2-14
 */
public class PoolImpl implements Pool {
    private Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    @Override
    public Channel getChannelByIp(String ip) {
        return channelMap.get(ip);
    }

    @Override
    public void putChannel(String ip,Channel channel) {
        channelMap.put(ip, channel);
    }
}
