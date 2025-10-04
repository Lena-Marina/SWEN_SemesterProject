package at.technikum.application.mrp.exception;


import at.technikum.server.http.Status;
import at.technikum.server.http.ContentType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HttpError {
    Status status();
    ContentType contentType() default ContentType.TEXT_PLAIN;
}