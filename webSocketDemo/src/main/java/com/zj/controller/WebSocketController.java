package com.zj.controller;

import com.zj.webSocket.server.WebSocketServer;

import org.springframework.web.bind.annotation.*;

import javax.websocket.Session;
import java.io.IOException;

@RestController
@RequestMapping(value = "/api/ws")
public class WebSocketController {

    /**
     * 向指定客户端id发送一条消息
     *
     * @param id 客户端id
     * @param message 消息内容
     */
    @GetMapping("/sendOne")
    public String sendOne(@RequestParam(required = true) String id, @RequestParam(required = true) String message) {
        //调用webSocket处理类中发消息的方法
        try {
            WebSocketServer.SendMessage(id, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "消息发送成功";
    }
}
