package at.technikum.application.mrp.repository;

import java.util.List;
import java.util.Optional;

public interface MrpRepository<T> {

    Optional<T> find(String id); //warum Optional? -> Weil wir vll nichts finden

    List<T> findAll(); //warum Nicht Optional? -> wenn wir nichts finden, ist es einfach eine leere Liste

    T save(T object);

    T update(T object); //diese Funktion existiert nicht immer

    T delete(String id);
}
