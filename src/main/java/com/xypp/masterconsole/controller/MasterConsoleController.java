package com.xypp.masterconsole.controller;

import com.xypp.masterconsole.model.ResultMsg;
import com.xypp.masterconsole.wsserver.WebSocketServer;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "master")
public class MasterConsoleController {

    @RequestMapping(value = "hi")
    public String index(){
        return "hello";
    }

    /**
     * 启动前端所有的采集大师
     * @return
     */
    @SneakyThrows
    @RequestMapping(value = "/start/{url}")
    public ResultMsg start(@PathVariable String url){
        WebSocketServer.sendInfo("start",url);
        return ResultMsg.success();
    }

}
