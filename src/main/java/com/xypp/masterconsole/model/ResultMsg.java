package com.xypp.masterconsole.model;

import lombok.Data;

@Data
public class ResultMsg {

    private Integer code;
    private String msg;
    private Object data;

    public static ResultMsg success(){
        ResultMsg resultMsg = new ResultMsg();
        resultMsg.setCode(0);
        resultMsg.setMsg("success");
        return resultMsg;
    }

    public static ResultMsg success(Object data){
        ResultMsg resultMsg = new ResultMsg();
        resultMsg.setCode(0);
        resultMsg.setMsg("success");
        resultMsg.setData(data);
        return resultMsg;
    }

    public static ResultMsg error(String errorMsg) {
        ResultMsg resultMsg = new ResultMsg();
        resultMsg.setCode(-1);
        resultMsg.setMsg(errorMsg);
        return resultMsg;
    }

    public static ResultMsg error(Integer code, String errorMsg) {
        ResultMsg resultMsg = new ResultMsg();
        resultMsg.setCode(code);
        resultMsg.setMsg(errorMsg);
        return resultMsg;
    }

    public static ResultMsg systemError() {
        ResultMsg resultMsg = new ResultMsg();
        resultMsg.setCode(-2);
        resultMsg.setMsg("system error");
        return resultMsg;
    }


}
