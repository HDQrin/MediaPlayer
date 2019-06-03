package musicplayer.cn.musicplayer.util;

public class Result<T> {

    private Integer code = null;

    private String message = null;

    private T value = null;

    public Result(){}
    public Result(Integer code, String message, T value) {
        this.code = code;
        this.message = message;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}