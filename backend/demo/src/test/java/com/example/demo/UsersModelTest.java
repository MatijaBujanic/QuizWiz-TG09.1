package com.example.demo;

import com.example.demo.model.Users;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsersModelTest {

    @Test
    void testUsersCreation() {
        // Radimo Users-a
        Users user = new Users();
        user.setUserId(1L);
        user.setUsername("Ivan Horvat");
        user.setEmail("ivan.horvat@gmail.com");
        user.setPassword("password123");
        user.setContact_number("1234567890");
        user.setRole("Admin");

        // Provjera rezultata
        assertEquals(1L, user.getUserId());
        assertEquals("Ivan Horvat", user.getUsername());
        assertEquals("ivan.horvat@gmail.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("1234567890", user.getContact_number());
        assertEquals("Admin", user.getRole());
    }

    @Test
    void testUsersEdgeCases() {
        // Rubni slučaj s null i praznim vrijednostima
        Users user = new Users();
        user.setUserId(null);
        user.setUsername("");
        user.setEmail(null);
        user.setPassword("");
        user.setContact_number(null);
        user.setRole("");

        assertNull(user.getUserId());
        assertEquals("", user.getUsername());
        assertNull(user.getEmail());
        assertEquals("", user.getPassword());
        assertNull(user.getContact_number());
        assertEquals("", user.getRole());
    }

    @Test
    void testUsersWithSpecialCharacters() {
        // Rubni slučaj korisnik s posebnim znakovima
        Users user = new Users();
        user.setUsername("Ivan_Ivić@#$%");
        user.setEmail("test+tag@example.com");
        user.setPassword("P@ssw0rd!#$");
        user.setRole("admin-user");

        assertEquals("Ivan_Ivić@#$%", user.getUsername());
        assertEquals("test+tag@example.com", user.getEmail());
        assertEquals("P@ssw0rd!#$", user.getPassword());
        assertEquals("admin-user", user.getRole());
    }

}
