package at.technikum.application.mrp.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MrpRepository<T> {

    Optional<T> find(UUID id); //warum Optional? -> Weil wir vll nichts finden

    List<T> findAll(); //warum Nicht Optional? -> wenn wir nichts finden, ist es einfach eine leere Liste

    Optional<T> create(T object);

    Optional<T> update(T object); //diese Funktion existiert nicht immer

    T delete(UUID id);
}
