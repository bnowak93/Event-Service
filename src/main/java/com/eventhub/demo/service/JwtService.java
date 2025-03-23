package com.eventhub.demo.service;

public interface JwtService {

    /**
     * Extract the username form a JWT token
     * @param token the JWT token
     * @return username contained in the token
     */
    String extractUsername(String token);

    /**
     * Extract a specific token from the claim
     * @param token the JWT token
     * @param claimName the name of the claim to extract
     * @return value of the claim
     */
    Object extractClaim(String token, String claimName);

    /**
     * Checks if token is valid and not expired
     * @param token JWT token
     * @return true if token is valid, otherwise false
     */
    boolean isTokenValid(String token);

}
