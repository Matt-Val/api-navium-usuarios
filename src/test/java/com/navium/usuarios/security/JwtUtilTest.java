package com.navium.usuarios.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void generarToken_incluyeSubjectRolYExpiracion() {
        JwtUtil util = new JwtUtil();
        String secret = "NaviumSuperSecretKeyParaFirmaDeTokens2026!";
        ReflectionTestUtils.setField(util, "secret", secret);

        long start = System.currentTimeMillis();
        String token = util.generarToken("test@navium.com", "ROL_OPERADOR");

        Claims claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
            .build()
            .parseClaimsJws(token)
            .getBody();

        assertEquals("test@navium.com", claims.getSubject());
        assertEquals("ROL_OPERADOR", claims.get("rol"));
        assertNotNull(claims.get("roles"));
        assertTrue(((java.util.List<?>) claims.get("roles")).contains("ROL_OPERADOR"));

        Date expiration = claims.getExpiration();
        assertNotNull(expiration);

        long maxExp = start + 1000L * 60 * 60 * 10 + 2000L;
        assertTrue(expiration.getTime() >= start);
        assertTrue(expiration.getTime() <= maxExp);
    }
}
