import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HallListener implements Runnable {

	
	
	Socket socket;
	DataInputStream entrada;
	DataOutputStream salida;
	
	public HallListener(Socket socket, DataInputStream entrada, DataOutputStream salida) {
		this.socket = socket;
		this.entrada = entrada;
		this.salida = salida;
		System.out.println("Añadiendo hall.");
		Listados.halls.add(this);
	}
	
	@Override
	public void run() {
		
		
		try {enviarpartidas(Listados.informaraestehall());} catch (IOException e) {e.printStackTrace();	}
		
		
	}

	public void enviarpartidas(String json) throws IOException {
		
			salida.writeUTF("GETPARTIDAS%"+json);
		
	}

}
