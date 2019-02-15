package cn.lstfight.nettyproxy.util;

import io.netty.handler.codec.http.HttpRequest;

/**
 * <p>相关工具方法</p>
 *
 * @author 李尚庭
 * @date 2019-1-29
 */
public class Util {

    /**
     * <p>获取ip和port 未做合法性验证</p>
     * @param request
     * @return split[0]host split[1]port
     */
    public static String[] getHostAndPort(HttpRequest request) {
        if (null == request) {
            return null;
        }

        String host = request.headers().get("host");
        String[] split = host.split(":");

        if (split.length == 2) {
            return split;
        }
        //添加默认端口
        if (split.length == 1) {
            String[] spilt = {split[0], "80"};
            return spilt;
        }

        return null;
    }
}
