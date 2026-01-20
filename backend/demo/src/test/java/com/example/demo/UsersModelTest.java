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
        // Test edge cases with null and empty values
        Users user = new Users();
        user.setUserId(null);
        user.setUsername("");
        user.setEmail(null);
        user.setPassword("");
        user.setContact_number(null);
        user.setRole("");

        // Assert edge case values
        assertNull(user.getUserId());
        assertEquals("", user.getUsername());
        assertNull(user.getEmail());
        assertEquals("", user.getPassword());
        assertNull(user.getContact_number());
        assertEquals("", user.getRole());
    }

}
