package res.application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class RegisterService extends Service<String> {

	TextField usernamefield;
	PasswordField passwordfield;
	PasswordField reppassfield;
	String password;
	Button enter;
	Text info;
	String token;
	Global conexion;
	String response;
	String[] responseparts;
	

	public RegisterService(TextField usernamefield, PasswordField passwordfield, PasswordField reppassfield, Text info) {
		super();
		this.reppassfield = reppassfield;
		this.usernamefield = usernamefield;
		this.passwordfield = passwordfield;
		this.info = info;
	}

	@Override
	protected Task<String> createTask() {
		return new Task<String>() {
			@Override
			protected String call() throws Exception {
				
				if(!passwordfield.getText().equals(reppassfield.getText())) {return "401%Las contraseñas no coinciden.";}
				
				try {
					conexion = new Global();
					conexion.socket = new Socket(Global.IP, 5003);
					conexion.entrada = new DataInputStream(conexion.socket.getInputStream());
					conexion.salida = new DataOutputStream(conexion.socket.getOutputStream());
					conexion.salida.writeUTF("GETKEY");
					// Recibimos los datos de la clave pública y creamos una clave
					response = conexion.entrada.readUTF();
					responseparts = response.split("%");
					if (!responseparts[0].equals("GETKEY")) {return "5001";}
					
					RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(responseparts[1]), new BigInteger(responseparts[2]));
					KeyFactory factory = KeyFactory.getInstance("RSA");
					Global.pub = factory.generatePublic(spec);
					conexion = new Global();
					conexion.socket = new Socket(Global.IP, 5003);
					conexion.entrada = new DataInputStream(conexion.socket.getInputStream());
					conexion.salida = new DataOutputStream(conexion.socket.getOutputStream());
					//Enviamos el nombre de usuario y contraseñas solicitados encriptados con la clave pública.
					conexion.salida.writeUTF("REGISTER%" + encrypt(usernamefield.getText()) + "%" + encrypt(passwordfield.getText()));
				} catch (ConnectException e) {
					return "400";
				} catch (Exception e) {
					e.printStackTrace();
					return "500";
				}
				
				String response = conexion.entrada.readUTF();
				responseparts = response.split("%");

				switch (responseparts[0]) {
				
				case "401": return responseparts[0]+"%"+responseparts[1];
				
				case "200": break;
					
				default: return responseparts[0];
				}
				return response;

			}

		};

	}
	
	public static String encrypt(String texto) throws Exception {
	    Cipher encryptCipher = Cipher.getInstance("RSA");
	    encryptCipher.init(Cipher.ENCRYPT_MODE, Global.pub);

	    byte[] cipherText = encryptCipher.doFinal(texto.getBytes());

	    return Base64.getEncoder().encodeToString(cipherText);
	}

}