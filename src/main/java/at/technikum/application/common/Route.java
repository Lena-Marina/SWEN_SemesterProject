package at.technikum.application.common;

import at.technikum.server.http.Method;
import at.technikum.server.http.Response;

import java.util.regex.Pattern;

public class Route<T> {
    private final String path;
    private final T target; //entweder Subrouter im Mainrouter oder Controller-Handler-Funktionen in den Subroutern
    private final Method method;
    private final boolean isProtected;

    // Konstruktor für MainRouter ohne Methode und ohne isProtected
    public Route(String pathPrefix, T target) {
        this. method = null;
        this.path = pathPrefix;
        this.target = target;
        this.isProtected = false;
    }

    // Konstruktor für SubRouter mit Methode und mit isProtected
    public Route(Method method, String pathPart, T target, boolean isProtected) {
        this.method = method;
        this.path = pathPart;
        this.target = target;
        this.isProtected = isProtected;
    }

    public String getPathPrefix() {
        return path;
    }

    //mal schauen ob ich es noch brauche, wenn ich den Router ohne Regex schreibe (vllt im Controller)
    public Pattern getPathAsPattern() {
        String regex = "^" + path.replaceAll("\\{[^/]+\\}", "([^/]+)") + "$";
        return Pattern.compile(regex);
    }

    public T getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isProtected() {
        return isProtected;
    }
}