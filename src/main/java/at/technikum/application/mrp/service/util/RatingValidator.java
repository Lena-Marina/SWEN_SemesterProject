package at.technikum.application.mrp.service.util;

import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.Rating;

public class RatingValidator {

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
