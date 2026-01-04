package at.technikum.application.mrp.repository;

/*
* Wir haben grundsätzlich im Unterricht besprochen, dass sich unsere Tests auf die Business
* Logik konzentrieren sollten.
* Repositorys werden eher nicht getestet, da die meisten "Fehler" SQL-Exceptions wären.
*
* Aber in der Methode findFiltered baue ich die DB Abfrage Stück für Stück
* aus den im Request mitgegebenen Query-Parametern zusammen.
*
* Dabei handelt es sich um Logik, in welcher Fehler sein könnten.
* Außerdem wäre es aufwendig, diesen Teil händisch mit Postman zu testen.
*
* Daher machte ich hier 2 Tests, in welchen ich den DB-Anfrage-String gegen meine
* Erwartung teste.
* */

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.model.dto.MediaQuery;
import at.technikum.application.mrp.model.util.ModelMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MediaRepositoryTests {

    @Mock
    ConnectionPool connectionPool;

    @Mock
    Connection connection;

    @Mock
    PreparedStatement preparedStatement;

    @Mock
    ResultSet resultSet;

    @Mock
    ModelMapper mapper;

    @InjectMocks
    MediaRepository mediaRepository;

    @Test
    void given_not_null_queryparams_in_findFiltered_then_sql_contains_WHERE_clauses() throws Exception {
        //setup | arrange
        MediaQuery query = new MediaQuery();
        query.setTitle("example title");
        query.setGenre("HORROR");
        query.setMediaType("series");
        query.setReleaseYear(1996);
        query.setAgeRestriction(12);
        query.setRating(3);

        when(connectionPool.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); //ich will ja keine DB ergebnisse Testen

        //Mit dem ArgumentCaptor können wir den sql-String abfangen um ihn im Test auszuwerten
            //er ist ja eigentlich nur lokal in der Methode findFiltered() gespeichert
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        //call | act
        mediaRepository.findFiltered(query);

        //assertion | assert
        verify(connection).prepareStatement(sqlCaptor.capture()); //hier sagen wir dem ArgumentCaptor was er auffangen soll
        String sql = sqlCaptor.getValue();

        assertTrue(sql.contains("me.title LIKE ?"));
        assertTrue(sql.contains(" AND g.name LIKE ?"));
        assertTrue(sql.contains(" AND me.media_type LIKE ?"));
        assertTrue(sql.contains(" AND me.release_year = ?"));
        assertTrue(sql.contains(" AND me.age_restriction = ?"));
        assertTrue(sql.contains(" AND rating.avg_rating >= ?"));
    }

    //negativ Test wenn query Params Null, assertFalse statements
    @Test
    void given_all_null_params_in_findFiltered_then_sql_contains_only_WHERE_1_equals_1() throws Exception
    {
        //setup | arrange
        MediaQuery query = new MediaQuery();
        query.setTitle(null);
        query.setGenre(null);
        query.setMediaType(null);
        query.setReleaseYear(null);
        query.setAgeRestriction(null);
        query.setRating(null);

        when(connectionPool.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); //ich will ja keine DB ergebnisse Testen

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        //call | act
        mediaRepository.findFiltered(query);

        //assertion | assert
        verify(connection).prepareStatement(sqlCaptor.capture()); //hier sagen wir dem ArgumentCaptor was er auffangen soll
        String sql = sqlCaptor.getValue();

        assertTrue(sql.contains("WHERE 1=1"));
        assertFalse(sql.contains("me.title LIKE ?"));
        assertFalse(sql.contains(" AND g.name LIKE ?"));
        assertFalse(sql.contains(" AND me.media_type LIKE ?"));
        assertFalse(sql.contains(" AND me.release_year = ?"));
        assertFalse(sql.contains(" AND me.age_restriction = ?"));
        assertFalse(sql.contains(" AND rating.avg_rating >= ?"));
    }

    //Weitere Logik für Service:
    // muss validieren, dass mediaQuery.getGenre() einem akzeptierten Genre entspricht
    // muss validieren, dass mediaQuery.getMediaType() einem der akzeptierten mediaTypes entpricht
    // muss validieren, dass mediaQuery.getReleaseYear() ein valides Jahr ist (>= 0 && < this.year?)
    // muss validieren, dass mediaQuery.getAgeRestriction() eine valide AgeRestriction ist
    // muss validieren, dass mediaQuery.getRating() eine valides Rating ist (>0 && <6)


    //setup | arrange

    //call | act

    //assertion | assert

}
