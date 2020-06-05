package com.xypp.masterconsole.controller;

import com.alibaba.fastjson.JSON;
import com.xypp.masterconsole.model.ResultMsg;
import com.xypp.masterconsole.ws.WebSocketServer;
import com.xypp.masterconsole.ws.msg.WsServerMsg;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "master")
public class MasterConsoleController {

    @RequestMapping(value = "hi")
    public String index() {
        return "hello";
    }

    /**
     * 启动前端所有的采集大师
     *
     * @return
     */
    @SneakyThrows
    @RequestMapping(value = "/start/{url}", method = RequestMethod.POST)
    public ResultMsg start(@RequestBody Map<String, Object> params, @PathVariable String url) {
        WsServerMsg msg = new WsServerMsg();
        msg.setMessage("start");
        msg.setParams(params);
        log.info("start Url : " + url + ";params:" + JSON.toJSONString(params));
        return ResultMsg.success(WebSocketServer.sendServerMsg(msg, url));
    }

}
