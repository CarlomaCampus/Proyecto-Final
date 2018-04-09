import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class prueba {

	public static void main(String[] args) {
		String plaintext = "Hola este texto podría ser una contraseña y es perfectamente entendible.";
		String encripted;
		String decripted;
		String[] pkstrings;
		BigInteger modulus;
		BigInteger exponent;
		KeyPair kp;
		
		try {
		 kp = generateKeyPair();
		
		
		
		pkstrings = kp.getPublic().toString().split("\\r?\\n");
		
		
		modulus = new BigInteger(pkstrings[1].substring(11));
		exponent = new BigInteger(pkstrings[2].substring(19));
		
		
		RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		PublicKey pub = factory.generatePublic(spec);
		
		
		encripted = encrypt(plaintext, pub);
		
		decripted = decrypt(encripted, kp.getPrivate());
		
		System.out.println(encripted);
		System.out.println(decripted);
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public static KeyPair generateKeyPair() throws Exception {
	    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
	    generator.initialize(1024, new SecureRandom());
	    KeyPair pair = generator.generateKeyPair();

	    return pair;
	}
	
	public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
	    Cipher encryptCipher = Cipher.getInstance("RSA");
	    encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

	    byte[] cipherText = encryptCipher.doFinal(plainText.getBytes());

	    return Base64.getEncoder().encodeToString(cipherText);
	}
	
	public static String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
	    byte[] bytes = Base64.getDecoder().decode(cipherText);

	    Cipher decriptCipher = Cipher.getInstance("RSA");
	    decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

	    return new String(decriptCipher.doFinal(bytes));
	}

}


	