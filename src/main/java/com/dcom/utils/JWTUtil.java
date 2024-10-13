package com.dcom.utils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.dcom.dataModel.UserSessionInfo;

public class JWTUtil {
    private static final String SECRET_KEY = "kjasdbgojasug132b41!@235Z*(&*!&#%";

    public static UserSessionInfo validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withSubject("User Authentication")
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);

            int userId = decodedJWT.getClaim("userId").asInt();
            String userType = decodedJWT.getClaim("userType").asString();


            return new UserSessionInfo(userId, userType);
        } catch (JWTVerificationException exception) {
            System.out.println("Invalid or expired token: " + exception.getMessage());
            return null;
        }
    }
}
