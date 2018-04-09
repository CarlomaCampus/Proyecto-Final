import java.util.Base64;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class Authorization {
	public static final String TOKEN_KEY = "Bu$c@m1n4s0nl1nE";
	
	public static boolean isAuthorized(String token) {
		try {
			validateToken(token);
			return true;

		} catch (Exception exception) {
			return false;
		}
	}

//No va a ser bearer
//	private static boolean isTokenBasedAuthentication(String token) {
//		return token != null && token.toLowerCase().
//				startsWith(AUTHENTICATION_SCHEME + " ");
//	}

	private static void validateToken(String token) throws Exception {
		Algorithm algorithm = Algorithm.HMAC256(TOKEN_KEY);
		JWTVerifier verifier = JWT.require(algorithm).build();
		verifier.verify(token);
		
	}

	public static String getUserid(String token) {
		final String secret = Base64.getEncoder().encodeToString(TOKEN_KEY.getBytes());
		 Claims body = Jwts.parser()
                 .setSigningKey(secret)
                 .parseClaimsJws(token)
                 .getBody();

         
		return body.getSubject();
	}
	
	public static String getUsername(String token) {
		final String secret = Base64.getEncoder().encodeToString(TOKEN_KEY.getBytes());
		 Claims body = Jwts.parser()
                 .setSigningKey(secret)
                 .parseClaimsJws(token)
                 .getBody();

         
		return body.get("username").toString();
	}
}
