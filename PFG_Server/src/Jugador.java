import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;


public class Jugador implements Runnable {
	
	//Esta clase gestiona la conexion con cada jugador, y está ligado a una Partida
	DataInputStream entrada;
	DataOutputStream salida;
	Socket socket;
	Partida p;
	int puntuacion = 0;
	String nombre = "Player Unknown";
	int id;
	boolean repetido = false;
	
	public Jugador(String id, Socket socket, DataInputStream entrada, DataOutputStream salida, String username) {
		this.socket = socket;
		this.entrada = entrada;
		this.salida = salida;
		this.id = Integer.parseInt(id);
		this.nombre = username;
	}
	
	@Override
	public void run() {
		
		while(true && !repetido) {

			try {

				String query = entrada.readUTF();
				//El primer valor de este array será el objetico P.E: CREATE, y el resto de posiciones serán datos.
				String[] queryparts = query.split("%");
				
				
				switch(queryparts[0]) {
				
				case "GETPARTIDA": p = Listados.partidas.get(Integer.parseInt(queryparts[1])); p.ingresar(this); break;
				case "PULSARMINA": p.pulsarmina(this, Integer.parseInt(queryparts[1]), Integer.parseInt(queryparts[2])); break;
				case "PULSARBANDERA": p.pulsarbandera(this, Integer.parseInt(queryparts[1]), Integer.parseInt(queryparts[2])); break;
				default: System.out.println("No se ha entendido la petición: "+queryparts[0]); break;
				
				}
				
				
				
			} catch (SocketException e) {

				System.out.println("Conexión de "+socket.getInetAddress()+", puerto "+socket.getPort()+" reseteada. Cerrando y saliendo del bucle...");
				try {socket.close();} catch (IOException e1) {e1.printStackTrace();}
				break;
				
			} catch (EOFException e) {

				System.out.println("Conexión de "+socket.getInetAddress()+", puerto "+socket.getPort()+" reseteada. Cerrando y saliendo del bucle...");
				try {socket.close();} catch (IOException e1) {e1.printStackTrace();}
				break;
				
			} catch (Exception e) {
				//Posible IOException
				System.out.println("Excepción no esperada en la conexión "+socket.getInetAddress()+", puerto "+socket.getPort()+". Cerrando y saliendo del bucle...");
				e.printStackTrace();
				try {socket.close();} catch (IOException e1) {e1.printStackTrace();}
				break;
				
			}

		}
		
	}
	
	
	

}
