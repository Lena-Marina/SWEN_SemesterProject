package at.technikum.application.mrp.model.dto;

public class RecommendationRequest {
    String userId; //soll später UUID sein, denke ich
    String type; //möglichkeiten: content und genre

    public RecommendationRequest(String userId, String type) {
        this.userId = userId;
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }
}
