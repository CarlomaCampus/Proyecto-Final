package res.application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class LoginService extends Service<String> {

	TextField username;
	PasswordField password;
	Button enter;
	Text info;
	String token;
	Global conexion;

	public LoginService(TextField username, PasswordField password, Text info) {
		super();
		this.username = username;
		this.password = password;
		this.info = info;
	}

	@Override
	protected Task<String> createTask() {
		return new Task<String>() {
			@Override
			protected String call() throws Exception {
				Global.IP = (TokenUtils.obtenerDatosGuardados(TokenUtils.IP_TXT_PATH));
				try {
					conexion = new Global();
					conexion.socket = new Socket(Global.IP, 5003);
					conexion.entrada = new DataInputStream(conexion.socket.getInputStream());
					conexion.salida = new DataOutputStream(conexion.socket.getOutputStream());
					conexion.salida.writeUTF("LOGIN%" + username.getText() + "%" + encriptar(password.getText()));
				} catch (ConnectException e) {
					return "400";
				} catch (Exception e) {
					e.printStackTrace();
					return "500";
				}
				String response = conexion.entrada.readUTF();
				String[] responseparts = response.split("%");

				switch (responseparts[0]) {
				case "200":
					TokenUtils.guardarInfo(username.getText(), responseparts[1]);
					Global.info = username.getText()+"%"+responseparts[1];
					TokenUtils.guardarToken(responseparts[2]);
					Global.setToken(responseparts[2]);
					return responseparts[0];
				default: return responseparts[0];
				}

			}

			private String encriptar(String pass) {
				MessageDigest md;
				try {
					md = MessageDigest.getInstance("SHA-256");
					byte dataBytes[] = pass.getBytes();
					md.update(dataBytes);
					String result = Base64.getEncoder().encodeToString(md.digest());
					return result;
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
					return null;
				}

			}

		};

	}

}