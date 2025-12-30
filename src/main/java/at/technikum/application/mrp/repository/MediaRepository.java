package at.technikum.application.mrp.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.model.Media;

import java.util.List;
import java.util.Optional;

public class MediaRepository implements MrpRepository<Media>{
    private final ConnectionPool connectionPool;

    public MediaRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }


    @Override
    public Optional<Media> find(String id) {
        return Optional.empty();
    }

    @Override
    public List<Media> findAll() {
        return List.of();
    }

    @Override
    public Optional<Media> create(Media object) {
        return null;
    }

    @Override
    public Media update(Media object) {
        return null;
    }

    @Override
    public Media delete(String id) {
        return null;
    }
}
