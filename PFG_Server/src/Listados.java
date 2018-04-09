import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Listados {
	

	public static ArrayList<Partida> partidas = new ArrayList<>(); //aquí se almacenan las partidas, con sus métodos, etc
	public static ArrayList<PartidaModelo> partidasModelo = new ArrayList<>(); //aquí se almacena lo mismo pero sin el array de jugadores, para parsear a json fácilmente
	public static ArrayList<HallListener> halls = new ArrayList<>();
	
	
	
	public static void informaraloshalls() {
		

		System.out.println("Informando a " + halls.size() + " halls.");
		
		synchronized (halls) {
			
			for (int i = 0; i < halls.size(); i++) {

				System.out.println("Informando al hall número " + (i + 1) + "/" + halls.size());
				try {
					halls.get(i).enviarpartidas(new Gson().toJson(partidasModelo));
				} catch (IOException e) {
					// Si la conexión está cerrada, no volveremos a intentar contactar con este
					// hall. Así las ventanas cerradas no están consumiendo recursos del servidor
					System.out.println("Eliminando conexión de la lista.");
					halls.remove(i);
					i--;
				}

			}

		}
		
	}



	public static String informaraestehall() {
		
		return new Gson().toJson(partidasModelo);
		
	}

}
