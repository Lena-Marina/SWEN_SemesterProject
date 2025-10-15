package at.technikum.application.common;

import java.util.List;

public interface Service<T> {
    public void create(T object);

    public T get(T object);

    public List<T> getAll();

    public T update(T object);

    public T delete(T object);
}
