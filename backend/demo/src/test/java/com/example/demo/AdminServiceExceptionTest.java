package com.example.demo;

import com.example.demo.service.AdminService;
import com.example.demo.service.SupabaseService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceExceptionTest {

    @Test
    void testCreateUserThrowsExceptionOnNullEmail() {
        // Testiranje baca li se exception kada je email null
        UserService userService = mock(UserService.class);
        SupabaseService supabaseService = mock(SupabaseService.class);
        AdminService adminService = new AdminService(userService, supabaseService);

        assertThrows(IllegalArgumentException.class, () -> {
            adminService.createUser("testuser", null);
        });
    }

}
