import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Listados {
	

	public static ArrayList<Partida> partidas = new ArrayList<>(); //aqu� se almacenan las partidas, con sus m�todos, etc
	public static ArrayList<PartidaModelo> partidasModelo = new ArrayList<>(); //aqu� se almacena lo mismo pero sin el array de jugadores, para parsear a json f�cilmente
	public static ArrayList<HallListener> halls = new ArrayList<>();
	
	
	
	public static void informaraloshalls() {
		

		System.out.println("Informando a " + halls.size() + " halls.");
		
		synchronized (halls) {
			
			for (int i = 0; i < halls.size(); i++) {

				System.out.println("Informando al hall n�mero " + (i + 1) + "/" + halls.size());
				try {
					halls.get(i).enviarpartidas(new Gson().toJson(partidasModelo));
				} catch (IOException e) {
					// Si la conexi�n est� cerrada, no volveremos a intentar contactar con este
					// hall. As� las ventanas cerradas no est�n consumiendo recursos del servidor
					System.out.println("Eliminando conexi�n de la lista.");
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
