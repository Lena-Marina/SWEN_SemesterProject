package at.technikum.application.mrp.model.dto;

import java.util.UUID;

public class RecommendationRequest {
    UUID userId;
    String type; //möglichkeiten: content und genre

    public RecommendationRequest(UUID userId, String type) {
        this.userId = userId;
        this.type = type;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }
}
