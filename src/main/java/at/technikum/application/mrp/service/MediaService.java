package at.technikum.application.mrp.service;

import at.technikum.application.mrp.model.Genre;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.dto.RecommendationRequest;

import java.util.ArrayList;
import java.util.List;

public class MediaService {

    public List<Media> getRecommendation(RecommendationRequest dto)
    {
       //just for now - Mockdaten/ später unterscheidung nach type und weitergabe an Repo:
        List<Media> recommendations = new ArrayList<>();
        recommendations.add(new Media("1234", "Mein Freund Harvey", "movie", 1950,12 ));
        recommendations.add(new Media("12345", "Ame & Yuki", "movie", 2012, 12));

        return recommendations;
    }

    public void markAsFavorite(String id)
    {
        //schauen ob Media Id existieren
        //vermutlich auch eher das markierte Media zurückgeben

    }

    public void unmarkAsFavorite(String id)
    {
        //schauen ob Media id existiert
        //vermutlich auch eher das markierte Media zurückgeben
    }
}
