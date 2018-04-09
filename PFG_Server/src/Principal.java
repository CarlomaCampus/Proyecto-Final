import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import javax.persistence.NoResultException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;

import domain.Usuarios;
 

public class Principal {
	
	
	static ArrayList<Jugador> clientes;
	
	
	public static void main(String[] args) {


		Seguridad seguridad = new Seguridad();
		System.out.println("Servidor iniciado.");
		
		
		clientes = new ArrayList<>();
		try {
			// El servidor escuchará desde este serversocket en bucle y guardará las
			// conexiones con los usuarios en objetos hilo Jugador
			@SuppressWarnings("resource")
			ServerSocket ss = new ServerSocket(5003);


			while (true) {
				System.out.println("Esperando conexión...");
				Socket socket = ss.accept();
				System.out.println(
						"Conexión establecida con IP " + socket.getInetAddress() + " en el puerto " + socket.getPort());
				DataInputStream entrada = new DataInputStream(socket.getInputStream());
				DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
				// realiza acciones distintas si se conecta desde el hall o como una instancia
				// de jugador
				try {
					String[] responseparts = entrada.readUTF().split("%");
					
					
						 //Si el token es válido te permitirá crear una conexión
							
					switch (responseparts[0]) {
					case "LOGIN": login(responseparts[1], responseparts[2], salida); break;
					case "CONNECT":
						if(Authorization.isAuthorized(responseparts[2])) { //token
							switch (responseparts[1]) {
	
							case "HALL":
								new Thread(new Hall(socket, entrada, salida)).start(); break;
							case "JUGADOR":
								new Thread(new Jugador(Authorization.getUserid(responseparts[2]), socket, entrada, salida, Authorization.getUsername(responseparts[2]))).start(); break;
							case "HALLLISTENER":
								new Thread(new HallListener(socket, entrada, salida)).start(); break;
							
							default: System.out.println("No se ha entendido el tipo de cliente.");
						}
						
						System.out.println("El cliente es un " + responseparts[1]); break;
						} else {System.out.println("Usuario no autorizado."); salida.writeUTF("401");}
					case "GETKEY": salida.writeUTF("GETKEY%"+seguridad.modulus+"%"+seguridad.exponent); break;
					case "REGISTER": registrar(responseparts, seguridad, salida); break;
					default: System.out.println("Se esperaba una petición de conexión y ha sido: " + responseparts[0]); break;
					
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void registrar(String[] responseparts, Seguridad seguridad, DataOutputStream salida) throws Exception {
			
			String username = seguridad.decrypt(responseparts[1]);
			String password = seguridad.decrypt(responseparts[2]);
			
			
			System.out.println("Escribiendo");
			FileWriter filewriter;
			try { filewriter = new FileWriter("accounts.txt", true); 
			PrintWriter printwriter = new PrintWriter(filewriter);
			printwriter.println(username+"%"+password);
			printwriter.close();
			} catch (IOException e) { e.printStackTrace();}
			
			
			if(username.length() <= 5 || username.length() >= 25) {salida.writeUTF("401%El nombre de usuario debe tener entre 5 y 25 carácteres.");}
			else if (password.length() < 10 || password.length() > 25) {salida.writeUTF("401%La contraseña debe tener entre 10 y 25 carácteres.");}
			else if (!StringUtils.isAlphanumeric(username)) {salida.writeUTF("401%El nombre de usuario debe tener solamente carácteres alfanuméricos.");}
			else if (!StringUtils.isAlphanumeric(password)) {salida.writeUTF("401%La contraseña debe tener solamente carácteres alfanuméricos.");}
			else {
			
			System.out.println("Registrando al usuario: " + username);
			
				try {
					HibernateSession.getTransaction();

					// no solo comprobamos si hay un usuario con esa contraseña sino que le ponemos
					// un id al objeto usuario
					Usuarios usuario = new Usuarios(username, encriptar(password), 0);
					HibernateSession.session.save(usuario);
					HibernateSession.commitclose();
					System.out.println("200");
					salida.writeUTF("200");
				} catch (ConstraintViolationException e) {
					HibernateSession.rollbackclose();
					System.out.println("ConstraintViolationException");
					salida.writeUTF("401%Ya existe ese nombre de usuario.");	
				} catch (Exception e) {
					e.printStackTrace();
					HibernateSession.rollbackclose();
					System.out.println("500");
					salida.writeUTF("500");				
				}
			}
	}

	private static void login(String username, String password, DataOutputStream salida) {
	
		System.out.println("Logueando al usuario: " + username);
		try {
			try {
				HibernateSession.getTransaction();

				// no solo comprobamos si hay un usuario con esa contraseña sino que le ponemos
				// un id al objeto usuario
				Usuarios usuario = (Usuarios) HibernateSession.session.createQuery("from Usuarios where username = '"
						+ username + "' AND password = '" + password + "'").getSingleResult(); 
				HibernateSession.commitclose();
				String prueba = "200%"+usuario.getPuntos()+"%" + generateToken(usuario.getIdusuario(), username);
				salida.writeUTF(prueba);
				System.out.println(prueba);
				System.out.println("200");
			} catch (NoResultException e) {
				HibernateSession.commitclose();

				salida.writeUTF("401");
				System.out.println("401");
			} catch (Exception e2) {
				HibernateSession.rollbackclose();
				e2.printStackTrace();
				System.out.println("500");
				salida.writeUTF("500");				
			}
		} catch (Exception e1) {
			HibernateSession.commitclose();
			e1.printStackTrace();
			System.out.println("500");
		}

	}
	

	private static String generateToken(int id, String username) {
		String token = null;
		// Se preparan y formatean las fechas
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		// Se define el algoritmo con la clave elegida
		try {
		Date issuedDate = format.parse(LocalDate.now().toString());
		Date expirationDate = format.parse(LocalDate.now().plusMonths(1).toString());
		Algorithm algorithm = Algorithm.HMAC256(Authorization.TOKEN_KEY);
		// Se crea el token
		token = JWT.create()
				.withSubject(String.valueOf(id))
				.withIssuedAt(issuedDate)
				.withExpiresAt(expirationDate)
				.withClaim("username", username)
				.sign(algorithm);
		} catch (IllegalArgumentException | UnsupportedEncodingException | ParseException e) {e.printStackTrace();}
		return token;
	}
	
	private static String encriptar(String pass) {
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


}
