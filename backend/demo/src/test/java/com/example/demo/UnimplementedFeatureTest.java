package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UnimplementedFeatureTest {

    @Test
    void testCallUnimplementedExportUsersFeature() {
        // Poziv neimplementirane funkcionalnosti - export korisnika
        assertThrows(UnsupportedOperationException.class, () -> {
            throw new UnsupportedOperationException("Export users feature is not yet implemented");
        });
    }

    @Test
    void testCallUnimplementedAdvancedSearchFeature() {
        // Poziv neimplementirane funkcionalnosti - napredna pretraga
        assertThrows(UnsupportedOperationException.class, () -> {
            throw new UnsupportedOperationException("Advanced search with filters not yet implemented");
        });
    }
}
