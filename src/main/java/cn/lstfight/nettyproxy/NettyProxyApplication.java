package cn.lstfight.nettyproxy;

import cn.lstfight.nettyproxy.httpproxy.ProxyServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 李尚庭
 * @date 2019-1-29
 */
@SpringBootApplication
public class NettyProxyApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(NettyProxyApplication.class, args);
        try {
            int port = Integer.parseInt(args[0]);
            System.out.println();
            ProxyServer proxyServer = new ProxyServer(port);
            proxyServer.run();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 异步调用的
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {

    }
}

