package at.technikum.application.mrp.model.util;

import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;

import java.time.Year;

public class ModelValidator {

    public void validateMediaOrThrow(Media media) {
        if (media == null) {
            throw new IllegalArgumentException("Media must not be null");
        }

        // Pflichtfelder
        if (media.getId() == null) {
            throw new IllegalArgumentException("Media id must not be null");
        }
        if (media.getTitle() == null || media.getTitle().isBlank()) {
            throw new IllegalArgumentException("Media title must not be null or blank");
        }
        if (media.getDescription() == null || media.getDescription().isBlank()) {
            throw new IllegalArgumentException("Media description must not be null or blank");
        }
        if (media.getMediaType() == null) {
            throw new IllegalArgumentException("Media type must not be null");
        }
        if (media.getReleaseYear() == null) {
            throw new IllegalArgumentException("Release year must not be null");
        }
        if (media.getAgeRestriction() == null) {
            throw new IllegalArgumentException("Age restriction must not be null");
        }
        if (media.getCreatorID() == null) {
            throw new IllegalArgumentException("Creator ID must not be null");
        }

        // Listen müssen existieren
        if (media.getGenres() == null) {
            throw new IllegalArgumentException("Genres list must be initialized");
        }
        if (media.getFavoritedBy() == null) {
            throw new IllegalArgumentException("FavoritedBy list must be initialized");
        }
        if (media.getRatings() == null) {
            throw new IllegalArgumentException("Ratings list must be initialized");
        }

        // mediaType validieren
        if (!"series".equals(media.getMediaType()) &&
                !"movie".equals(media.getMediaType()) &&
                !"game".equals(media.getMediaType())) {
            throw new IllegalArgumentException("mediaType must be 'series', 'movie' or 'game'");
        }

        // ageRestriction: 0–18
        if (media.getAgeRestriction() < 0 || media.getAgeRestriction() > 18) {
            throw new IllegalArgumentException("ageRestriction must be between 0 and 18");
        }

        // releaseYear: 0–aktuelles Jahr
        int currentYear = Year.now().getValue();
        if (media.getReleaseYear() < 0 || media.getReleaseYear() > currentYear) {
            throw new IllegalArgumentException("releaseYear must be between 0 and " + currentYear);
        }

        // averageScore: 0–5
        if (media.getAverageScore() != null && (media.getAverageScore() < 0.0f || media.getAverageScore() > 5.0f)){
            throw new IllegalArgumentException("averageScore must be between 0 and 5");
        }
    }


    public void removeCommentIfNotConfirmed(Rating rating) {
        if (!rating.getConfirmed()) {
            rating.setComment("");
        }
    }

    public void removeCommentsIfNotConfirmedFromMedia(Media media) {
        if (media.getRatings() == null) return;

        for (Rating rating : media.getRatings()) {
            removeCommentIfNotConfirmed(rating);
        }

    }
}
