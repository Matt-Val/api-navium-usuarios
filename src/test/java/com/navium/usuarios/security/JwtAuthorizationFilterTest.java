package com.navium.usuarios.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthorizationFilterTest {

    private static final String SECRET = "NaviumSuperSecretKeyParaFirmaDeTokens2026!";

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_sinHeader_noAutentica() throws Exception {
        JwtAuthorizationFilter filter = new JwtAuthorizationFilter();
        ReflectionTestUtils.setField(filter, "secret", SECRET);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_tokenValido_autentica() throws Exception {
        JwtAuthorizationFilter filter = new JwtAuthorizationFilter();
        ReflectionTestUtils.setField(filter, "secret", SECRET);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        String token = Jwts.builder()
            .setSubject("test@navium.com")
            .setExpiration(new Date(System.currentTimeMillis() + 60000))
            .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
            .compact();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        filter.doFilterInternal(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("test@navium.com", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_tokenInvalido_limpiaContexto() throws Exception {
        JwtAuthorizationFilter filter = new JwtAuthorizationFilter();
        ReflectionTestUtils.setField(filter, "secret", SECRET);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("user", null));

        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }
}
