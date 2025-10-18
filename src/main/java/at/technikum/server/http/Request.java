package at.technikum.server.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Request {

    private Method method;
    private String path;
    private String body;
    private String authorizationHeader; //alle header speichern oder "ich brauche eh nur den authorisation header" ->also wäre auch nur ein String für diesen okay
    private Map<String, String> queryParams = new HashMap<>(); //okay aber hier macht eine Map Sinn, weil ich ja konkret key-value paare abbilden will, oder?

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
    public String getAuthorizationHeader() {
        return authorizationHeader;
    }

    public void setAuthorizationHeader(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public String extractId() { //Momentan mache ich es noch als String, weil ich nicht weiß wie ich
        //die echten UUIDs in Postmann geben soll
        String[] parts = this.path.split("/");
        if (parts.length > 2 && !parts[2].isEmpty()) {
            return parts[2];
        } else {
            throw new IllegalArgumentException("Path does not contain a valid ID: " + this.path);
        }
    }

    //Debugging: ganzen Request ausgeben
    @Override
    public String toString() { // habe ich mir ehrlich gesagt von ChatGPT schreiben lassen ,
                                //da es nicht direkt Teil der Aufgabe ist, sondern mir beim Debugging hilft,
                                //hoffe ich das ist okay.
        StringBuilder sb = new StringBuilder();
        sb.append("Request {")
                .append("\n  method = ").append(method)
                .append(",\n  path = '").append(path).append('\'')
                .append(",\n  authorizationHeader = '").append(authorizationHeader).append('\'');

        if (queryParams != null && !queryParams.isEmpty()) {
            sb.append(",\n  queryParams = {");
            queryParams.forEach((key, value) ->
                    sb.append("\n    ").append(key).append(" = '").append(value).append('\'')
            );
            sb.append("\n  }");
        } else {
            sb.append(",\n  queryParams = {}");
        }

        sb.append(",\n  body = '").append(body).append('\'')
                .append("\n}");

        return sb.toString();
    }
}
