package websystems.domains.webclient;

import com.google.gson.JsonElement;

public class ResponseClient {
    private String timestamp;
    private Integer code;
    private String status;
    private String message;
    private JsonElement result;
    private String path;

    public ResponseClient() {
    }

    public ResponseClient(String timestamp, Integer code, String status, String message, JsonElement result, String path) {
        this.timestamp = timestamp;
        this.code = code;
        this.status = status;
        this.message = message;
        this.result = result;
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonElement getResult() {
        return result;
    }

    public void setResult(JsonElement result) {
        this.result = result;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
