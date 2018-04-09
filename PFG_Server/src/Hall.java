import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Hall implements Runnable {

	
	Socket socket;
	DataInputStream entrada;
	DataOutputStream salida;
	int idpartida;
	
	
	
	public Hall(Socket socket, DataInputStream entrada, DataOutputStream salida) {
		this.socket = socket;
		this.entrada = entrada;
		this.salida = salida;
	}

	


	@Override
	public void run() {
		
		while(true) {

			try {

				String query = entrada.readUTF();
				
				//El primer valor de este array será el objetico P.E: CREATE, y el resto de posiciones serán datos.
				String[] queryparts = query.split("%");
				
				
				switch(queryparts[0]) {
				
				
				case "CREATE": crearPartida(queryparts); break;
				
				default: System.out.println("El Hall no ha entendido la petición: "+queryparts[0]); break;
				
				}
				
				
				
			} catch (SocketException e) {

				System.out.println("Conexión de "+socket.getInetAddress()+", puerto "+socket.getPort()+" reseteada. Cerrando y saliendo del bucle...");
				try {socket.close();} catch (IOException e1) {e1.printStackTrace();}
				break;
				
			} catch (EOFException e) {

				System.out.println("Parece que se ha cerrado una conexión.");
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
	
	private void crearPartida(String[] queryparts) {
		
		//no queremos que un thread cree una partida mientras el otro cuenta cuantas partidas hay y se creen dos partidas con el mismo id
		//aunque esto solo ocurriría con muchísimas partidas creadas en un solo segundo
		System.out.println("Creando partida...");
		synchronized (Listados.partidas) {
			
		Listados.partidas.add(new Partida(Listados.partidas.size(), queryparts[1], (int)Double.parseDouble(queryparts[2]),  (int)Double.parseDouble(queryparts[3]),  (int)Double.parseDouble(queryparts[4]), (int)Double.parseDouble(queryparts[5])));
		try {salida.writeUTF(Listados.partidas.size()-1+"");} catch (IOException e) {e.printStackTrace();}
		System.out.println("Id enviado.");
		}
		
	}
	

}
