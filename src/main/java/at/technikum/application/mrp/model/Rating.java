package at.technikum.application.mrp.model;


import java.util.UUID;

public class Rating {
    private String id;
    private int stars;       // 1-5
    private String comment;
    private boolean confirmed;  // Kommentare müssen bestätigt werden

    private UUID user;       // Wer hat die Bewertung abgegeben
    private Media media;     // Auf welchen MediaEntry sich die Bewertung bezieht

    // Getter & Setter
    public void setId(String id){
            this.id=id;
        }
        public String getId(){
            return this.id;
        }

    public void setStars(int stars) {
            if(stars < 0 || stars > 5){
                throw new IllegalArgumentException("stars must be between 0 and 5");
            }
            this.stars = stars;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setUser(UUID userId) {
        this.user = userId;
    }

    public void setMedia(Media deletedMedia) {
        this.media = deletedMedia;
    }
}


