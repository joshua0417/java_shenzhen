package com.cet.pq.pqgovernanceservice.model.common;

import java.io.Serializable;

/**
 * @author CKai
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Result<T> implements Serializable {
	
	public static final int SUCCESS_CODE = 0;
	
	private static final long serialVersionUID = 1L;
	
    private Integer code;

    private String msg;

    private T data;

    public Result() {
        code = 0;
        msg = "";
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

	public static Result success() {
		return new Result(0,"操作成功",null);
    }
    
    public static <T> Result<T> success(T t){
    	return new Result<T>(0, "success", t);
    }

    public static <T> Result<T> success(String msg){
        return new Result<T>(0, msg, null);
    }
    
    public static Result error(String msg) {
		return new Result(500,msg,null);
    }
    public static <T> Result<T> error(T t) {
		return new Result(500,"error",t);
    }
    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
