package com.tmall.util;

import lombok.Getter;
import lombok.Setter;

/*
* 统一的 REST响应对象。 这个对象里包含是否成功，错误信息，以及数据。
* 附加了更多的信息，方便前端人员识别和显示给用户可识别信息
* */

public class Result {
    public static int SUCCESS_CODE = 0;
    public static int FAIL_CODE = 1;

    @Setter@Getter
    int code;
    @Getter@Setter
    String message;
    @Setter@Getter
    Object data;

    private Result(int code,String message,Object data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result success(){
        return  new Result(SUCCESS_CODE, null, null);
    }

    public static Result success(Object data){
        return new Result(SUCCESS_CODE,"",data);
    }

    public static Result fail(String message){
        return new Result(FAIL_CODE, message, null);
    }


}
