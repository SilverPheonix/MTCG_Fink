import org.example.Controller.UserController;
import org.example.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "password123");
    }

    @Test
    void testUserConstructor() {
        // Test the constructor and default values
        User newUser = new User("john_doe", "password123");
        assertEquals("john_doe", newUser.getUsername());
        assertEquals("password123", newUser.getPassword());
        assertEquals(20, newUser.getCoins());  // default value
        assertEquals(100, newUser.getElo());   // default value
    }

    @Test
    void testGettersAndSetters() {
        // Test setters and getters
        user.setUsername("newuser");
        user.setPassword("newpassword");
        user.setCoins(50);
        user.setElo(120);

        assertEquals("newuser", user.getUsername());
        assertEquals("newpassword", user.getPassword());
        assertEquals(50, user.getCoins());
        assertEquals(120, user.getElo());
    }

    @Test
    void testAddCardToStack() {
        // Test adding a card to the stack (You'd need a Card class for this, but this shows the idea)
        // Assuming we have a simple Card class:
        // Card card = new Card("Dragon", "Fire", 30);
        // user.addCardToStack(card);
        // assertEquals(1, user.getStack().size());
    }

    @Test
    void testUserDefaultValues() {
        // Test default values for coins and elo
        assertEquals(20, user.getCoins());  // default value
        assertEquals(100, user.getElo());   // default value
    }
}
