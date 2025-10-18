package at.technikum.application.mrp.repository;

import at.technikum.application.mrp.model.Media;

import java.util.List;
import java.util.Optional;

public class MediaRepository implements MrpRepository<Media>{

    @Override
    public Optional<Media> find(String id) {
        return Optional.empty();
    }

    @Override
    public List<Media> findAll() {
        return List.of();
    }

    @Override
    public Media save(Media object) {
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
