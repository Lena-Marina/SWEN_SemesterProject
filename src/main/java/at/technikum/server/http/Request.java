package at.technikum.server.http;

public class Request {

    private String method;
    private String path;
    private String bodyRaw;
    private String body;
    //private Map<String, String> header; //alle header speichern oder "ich brauche eh nur den authorisation header" ->also wäre auch nur ein String für diesen okay

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBody() {
        return body;
    }

    public String getBodyAsString() { return bodyRaw; }

    public void setBodyRaw(String body) { this.bodyRaw = body; }

}
