package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.EntityAlreadyExistsException;
import at.technikum.application.mrp.exception.EntityNotFoundException;
import at.technikum.application.mrp.exception.EntityNotSavedCorrectlyException;
import at.technikum.application.mrp.exception.InvalidEntityException;
import at.technikum.application.mrp.model.Genre;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.model.dto.UserCredentials;
import at.technikum.application.mrp.model.dto.UserUpdate;
import at.technikum.application.mrp.repository.FavoriteRepository;
import at.technikum.application.mrp.repository.RatingRepository;
import at.technikum.application.mrp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    /*in den Methoden der UserService-Klasse habe ich auch ein paar Möglichkeiten,
    dass Exceptions geworfen werden, welche nicht in eigene Methoden ausgelagert sind,
     weshalb ich mit dachte, dass ich das auch üben könnt zu testen  */

    @Mock
    UserRepository userRepository;

    @Mock
    RatingRepository ratingRepository;

    @Mock
    FavoriteRepository favoriteRepository;

    @InjectMocks
    UserService userService;

    private UserCredentials validCredentials;
    private User savedUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        validCredentials = new UserCredentials("Lena", "secret");
        savedUser = new User();
        savedUser.setId(userId);
        savedUser.setUsername("Lena");
        savedUser.setPassword("secret");
    }

    //tests registerUser()
    @Test
    void test_given_valid_credentials_when_registerUser_then_returns_user_with_null_password() {
        // setup | arrange
        when(userRepository.doesUserExist("Lena")).thenReturn(false);
        when(userRepository.create(any())).thenReturn(Optional.of(savedUser));

        // call | act
        User result = userService.registerUser(validCredentials);

        // assertion | assert
        assertEquals("Lena", result.getUsername());
        assertNull(result.getPassword());
        assertEquals(userId, result.getId());
    }

    @Test
    void test_given_empty_username_when_registerUser_then_throws_exception() {
        // setup | arrange
        UserCredentials creds = new UserCredentials("", "secret");

        // call | act && assertion | assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(creds));

        // assertion | assert
        assertEquals("Username and password cannot be empty", ex.getMessage());
    }

    @Test
    void test_given_existing_user_when_registerUser_then_throws_exception() {
        // setup | arrange
        when(userRepository.doesUserExist("Lena")).thenReturn(true);

        // call | act && assertion | assert
        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> userService.registerUser(validCredentials));

        // assertion | assert
        assertEquals("This Username is already taken!", ex.getMessage());
    }

    @Test
    void test_given_repo_returns_empty_optional_when_registerUser_then_throws_exception() {
        // setup | arrange
        when(userRepository.doesUserExist("Lena")).thenReturn(false);
        when(userRepository.create(any())).thenReturn(Optional.empty());

        // call | act && assertion | assert
        EntityNotSavedCorrectlyException ex = assertThrows(EntityNotSavedCorrectlyException.class,
                () -> userService.registerUser(validCredentials));

        // assertion | assert
        assertEquals("Unable to save user", ex.getMessage());
    }

    //tests getUserById()
    @Test
    void test_given_valid_userID_when_getUserByID_then_returns_user_with_null_password() {
        // setup | arrange
        when(userRepository.find(userId)).thenReturn(Optional.of(savedUser));

        // call | act
        User result = userService.getUserByID(userId.toString());

        // assertion | assert
        assertEquals("Lena", result.getUsername());
        assertNull(result.getPassword());
        assertEquals(userId, result.getId());
    }

    @Test
    void test_given_invalid_UUID_when_getUserByID_then_throws_exception() {
        // setup | arrange
        String invalidID = "not-a-uuid";

        // call | act && assertion | assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.getUserByID(invalidID));

        // assertion | assert
        assertTrue(ex.getMessage().contains("UserID is not a valid UUID"));
    }

    @Test
    void test_given_nonexistent_user_when_getUserByID_then_throws_exception() {
        // setup | arrange
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        // call | act && assertion | assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.getUserByID(userId.toString()));

        // assertion | assert
        assertEquals("User with id " + userId + " was not found", ex.getMessage());
    }

    @Test
    void test_given_user_with_empty_username_when_getUserByID_then_throws_exception() {
        // setup | arrange
        User userWithEmptyName = new User();
        userWithEmptyName.setId(userId);
        userWithEmptyName.setUsername("");

        // call | act
        when(userRepository.find(userId)).thenReturn(Optional.of(userWithEmptyName));

        // call | act && assertion | assert
        InvalidEntityException ex = assertThrows(InvalidEntityException.class,
                () -> userService.getUserByID(userId.toString()));

        assertEquals("Username is empty", ex.getMessage());
    }

    //tests updateUser()
    @Test
    void test_given_valid_update_when_updateUser_then_returns_updated_user() {
        // setup | arrange
        UserUpdate update = new UserUpdate();
        update.setUserID(userId);
        update.setEmail("new@email.com");
        update.setFavoriteGenre(Genre.HORROR);

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("Lena");
        existingUser.setEmail("old@email.com");
        existingUser.setFavoriteGenre(Genre.SCI_FI);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("Lena");
        updatedUser.setEmail("new@email.com");
        updatedUser.setFavoriteGenre(Genre.HORROR);

        // call | act
        when(userRepository.find(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.update(existingUser)).thenReturn(Optional.of(updatedUser));

        User result = userService.updateUser(update);

        // assertion | assert
        assertEquals("Lena", result.getUsername());
        assertEquals("new@email.com", result.getEmail());
        assertEquals(Genre.HORROR, result.getFavoriteGenre());
    }

    @Test
    void test_given_null_userID_when_updateUser_then_throws_exception() {

        // setup | arrange
        UserUpdate update = new UserUpdate();
        update.setUserID(null);

        // call | act && assertion | assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(update));

        // assertion | assert
        assertEquals("UserID cannot be empty", ex.getMessage());
    }

    @Test
    void test_given_nonexistent_user_when_updateUser_then_throws_exception() {
        // setup | arrange
        UserUpdate update = new UserUpdate();
        update.setUserID(userId);

        when(userRepository.find(userId)).thenReturn(Optional.empty());

        // call | act && assertion | assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(update));

        // assertion | assert
        assertEquals("User not found", ex.getMessage());
    }

}
