package org.pet.home.utils;


/**
 * @author 13
 * @qq交流群 719099151
 * @email 2449207463@qq.com
 * @link https://github.com/ZHENFENG13/My-BBS
 */
public class NetResult<T> {
    private static final long serialVersionUID = 1L;
    private int resultCode;
    private String message;
    private T data;

    public NetResult() {
    }

    public NetResult(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NetResult{" +
                "resultCode=" + resultCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
