package com.example.demo.security;

import java.lang.annotation.*;

/**
 * Annotation za zaštitu kontroler metoda prema ulogama
 * Primjer: @RequireRole({"ADMIN", "ORGANIZER"})
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    /**
     * Dozvoljene uloge
     */
    String[] value() default {};
}

