package com._pearls.cms.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtAccessDeniedHandlerTest {

    @Test
    @DisplayName("Should return 403 Forbidden with proper JSON format")
    void testHandle() throws IOException {
        JwtAccessDeniedHandler handler = new JwtAccessDeniedHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException accessDeniedException = new AccessDeniedException("Access denied");

        handler.handle(request, response, accessDeniedException);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        assertEquals("application/json", response.getContentType());
        
        String content = response.getContentAsString();
        assertTrue(content.contains("ACCESS_DENIED"));
        assertTrue(content.contains("Access Denied"));
    }
}
