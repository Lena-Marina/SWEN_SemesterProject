package at.technikum.application.common;

import at.technikum.server.http.Method;

import java.util.regex.Pattern;

public class Route<T> {
    private final String pathPrefix;
    private final T target; //entweder Subrouter im Mainrouter oder Controller-Handler-Funktionen in den Subroutern
    private final Method method;


    // Konstruktor für MainRouter ohne Methode
    public Route(String pathPrefix, T target) {
        this(null, pathPrefix, target);
    }

    // Konstruktor für SubRouter mit Methode
    public Route(Method method, String pathPrefix, T target) {
        this.method = method;
        this.pathPrefix = pathPrefix;
        this.target = target;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    //mal schauen ob ich es noch brauche, wenn ich den Router ohne Regex schreibe (vllt im Controller)
    public Pattern getPathAsPattern() {
        String regex = "^" + pathPrefix.replaceAll("\\{[^/]+\\}", "([^/]+)") + "$";
        return Pattern.compile(regex);
    }

    public T getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }
}