package at.technikum.application.mrp.model;


public class Rating {
        private String id;
        private int stars;       // 1-5
        private String comment;
        private boolean confirmed;  // Kommentare müssen bestätigt werden
        private int likes;

        private User user;       // Wer hat die Bewertung abgegeben
        private Media media;     // Auf welchen MediaEntry sich die Bewertung bezieht

        // Getter & Setter

        public void setId(String id){
            this.id=id;
        }

        public String getId(){
            return this.id;
        }
    }


