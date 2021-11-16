package com.zj.webSocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类
 */
@Configuration
public class WebSocketConfig {

    /**
     * 给spring容器注入ServerEndpointExporter对象
     *
     * @return 返回一个ServerEndpointExporter对象
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        System.out.println("------将ServerEndpointExporter注入了------");
        return new ServerEndpointExporter();
    }
}
