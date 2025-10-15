package at.technikum.server.http;

public class Request {

    private Method method;
    private String path;
    private String body;
    //private Map<String, String> header; //alle header speichern oder "ich brauche eh nur den authorisation header" ->also wäre auch nur ein String für diesen okay

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {

        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setBody(String body) {
        this.body = body;
    }
    public String getBody() {
        return body;
    }





}
