import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;

public class Seguridad {

	private KeyPair kp;
	public String modulus;
	public String exponent;
	
	public Seguridad() {
		
		try {
			
			this.kp = generateKeyPair();
		
			String [] pkstrings = kp.getPublic().toString().split("\\r?\\n");
			this.modulus = pkstrings[1].substring(11);
			this.exponent = pkstrings[2].substring(19);
		
		
		} catch (Exception e) {e.printStackTrace();}
		
			
		
	}
	
	private KeyPair generateKeyPair() throws Exception {
	    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
	    generator.initialize(1024, new SecureRandom());
	    KeyPair pair = generator.generateKeyPair();

	    return pair;
	}

	
	public String decrypt(String cipherText) throws Exception {
	    byte[] bytes = Base64.getDecoder().decode(cipherText);

	    Cipher decriptCipher = Cipher.getInstance("RSA");
	    decriptCipher.init(Cipher.DECRYPT_MODE, kp.getPrivate());

	    return new String(decriptCipher.doFinal(bytes));
	}
	
	
}
