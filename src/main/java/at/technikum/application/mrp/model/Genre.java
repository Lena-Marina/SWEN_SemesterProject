package at.technikum.application.mrp.model;

public enum Genre {
    HORROR("horror"),
    COMEDY("comedy"),
    ROMANCE("romance"),
    ACTION("action"),
    THRILLER("thriller"),
    DRAMA("drama"),
    SCI_FI("sci-fi"),
    FANTASY("fantasy"),
    DOCUMENTARY("documentary"),
    ANIMATION("animation"),
    ADVENTURE("adventure"),
    MYSTERY("mystery"),
    CRIME("crime"),
    MUSICAL("musical"),
    FAMILY("family");

    private final String verb;

    Genre(String verb) {
        this.verb = verb;
    }


    public String getVerb() {
        return verb;
    }

}
