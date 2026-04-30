package com.example.stock_market.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChaosControllerTest {

    @Test
    void chaosController_shouldExist() {
        ChaosController controller = new ChaosController();
        assertNotNull(controller);
    }

    @Test
    void chaosController_shouldHavePostMappingOnChaos() throws NoSuchMethodException {
        var method = ChaosController.class.getMethod("chaos");
        var annotation = method.getAnnotation(
                org.springframework.web.bind.annotation.PostMapping.class);
        assertNotNull(annotation);
        assertEquals("/chaos", annotation.value()[0]);
    }
}
