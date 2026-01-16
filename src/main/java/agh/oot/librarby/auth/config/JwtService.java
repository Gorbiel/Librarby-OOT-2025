package agh.oot.librarby.auth.config;

import agh.oot.librarby.auth.model.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for generating and validating JWT tokens.
 * Handles token creation, parsing, and validation for user authentication.
 */
@Service
public class JwtService {
    // TODO: Move to application.properties or environment variable in production
    private static final String SECRET_KEY = "bardzoTajnyKluczKtoryPowinienBycdlugiIskomplikowany123";
    private static final long TOKEN_VALIDITY_MS = 1000 * 60 * 60 * 24; // 24 hours

    /**
     * Extracts the subject (username) from the JWT token.
     *
     * @param token JWT token
     * @return username from the token
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the user ID from the JWT token.
     *
     * @param token JWT token
     * @return user ID as String, or null if not present
     */
    public String extractId(String token) {
        return extractClaim(token, claims -> {
            Object id = claims.get("id");
            if (id == null) {
                return null;
            }
            // Handle both Integer and Long types
            if (id instanceof Number) {
                return String.valueOf(((Number) id).longValue());
            }
            return String.valueOf(id);
        });
    }

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails the user details containing username and ID
     * @return generated JWT token
     */
    public String generateToken(CustomUserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .addClaims(Map.of("id", userDetails.getId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY_MS))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates if the token is valid for the given user.
     *
     * @param token       JWT token
     * @param userDetails user details to validate against
     * @return true if token is valid and not expired, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractSubject(token);
        return (username != null && username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Extracts a specific claim from the JWT token.
     *
     * @param token          JWT token
     * @param claimsResolver function to extract the specific claim
     * @param <T>            type of the claim
     * @return extracted claim value
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    /**
     * Checks if the token has expired.
     *
     * @param token JWT token
     * @return true if token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Retrieves the signing key for JWT token validation.
     *
     * @return the signing key
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
