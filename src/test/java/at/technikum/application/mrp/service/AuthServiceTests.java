package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.model.Token;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.model.dto.UserCredentials;
import at.technikum.application.mrp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTests {

    private UserRepository userRepositoryMock;
    private AuthService authService;

    @BeforeEach  //wird vor jedem Test ausgeführt
    void setup() {
        userRepositoryMock = Mockito.mock(UserRepository.class);
        authService = new AuthService(userRepositoryMock);
    }


    @Test //Erfolgreicher Login
    void testGetTokenSuccess() {
        // Vorbereitung
        UserCredentials creds = new UserCredentials();
        creds.setUsername("max");
        creds.setPassword("1234");

        User user = new User();
        user.setUsername("max");
        user.setPassword("1234");

        when(userRepositoryMock.findByCredentials("max", "1234"))
                .thenReturn(Optional.of(user));

        // Tatsächlicher Test
        Token token = authService.getToken(creds);

        assertEquals("max-mrpToken", token.getToken());
        verify(userRepositoryMock, times(1)) //verify prüft ob und wie oft die Methode aufgerufen wurde
                .findByCredentials("max", "1234");  //ist die Methode die im Mock Objekt aufgerufen werden soll
                                                                        //anstelle ihres tatsächlichen Rückgabewertes wird aber das oben
                                                                        //mit "when" angegebene Optional of das eine User Objekt zurück gegeben
    }


    @Test //User nicht gefunden (existiert nicht), EntityNotFoundException erwartet
    void test_ThrowExeption_IfUserNotInDB() {
        // Vorbereitung
        UserCredentials creds = new UserCredentials();
        creds.setUsername("max");
        creds.setPassword("1234");

        when(userRepositoryMock.findByCredentials("max", "1234"))
                .thenReturn(Optional.empty());

        // Tatsächlicher Test
        assertThrows(EntityNotFoundException.class, //wir erwarten, dass eine Exeption geworfen wird
                () -> authService.getToken(creds)); //wenn die Exception geworfen wird -> Test bestanden

        verify(userRepositoryMock, times(1))
                .findByCredentials("max", "1234");
    }
}
