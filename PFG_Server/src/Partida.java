import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import javax.persistence.NoResultException;

import domain.Usuarios;

public class Partida{
	
	String nombre;
	int cols, rows;
	int dificultad;
	int numplayers;
	int idpartida;
	ArrayList<Jugador> jugadores;
	boolean onair = false;
	PartidaModelo copiamodelo;
	int minas;
	int minasrestantes;
	int[][] tablero;
	boolean[][] tableromostrado;
	boolean cheating = false;
	
	
	public Partida(int idpartida, String nombre, int cols, int rows, int dificultad, int numplayers) {
		
		this.idpartida = idpartida;
		this.nombre = nombre;
		//por si los hackers la quieren liar modificando las querys al servidor
		if (cols <= 20 && cols >= 5) {this.cols = cols;} else {cheating = true;}
		if (rows <= 20 && rows >= 5) {this.rows = rows;} else {cheating = true;}
		if (dificultad <= 5 && dificultad >= 1) {this.dificultad = dificultad;}  else {cheating = true;}
		if (numplayers <= 10 && cols > 0) {this.numplayers = numplayers;} else {cheating = true;}
		if (cheating) {this.cols = 5; this.rows = 5; dificultad = 5; numplayers = 1;}
		
		jugadores = new ArrayList<>();
		System.out.println("Se ha creado una partida. Datos: "+nombre+" "+cols+" cols, "+rows+" rows, "+dificultad+" dif y "+numplayers+"  jugadores");
		copiamodelo = new PartidaModelo(idpartida,nombre,cols, rows, dificultad, numplayers);
		Listados.partidasModelo.add(copiamodelo);
		Listados.informaraloshalls();
		generartablero();
		
	}


	public void ingresar(Jugador jugador) {
		
		if (onair) {System.out.println("Error, la partida ya está comenzada.");} else {
			//Comprueba si el jugador ya está en esa partida
			for (int i = 0; i < jugadores.size(); i++) {
				if(jugadores.get(i).id == jugador.id) {jugador.repetido = true;}
			}	
			if (!jugador.repetido) {
				jugadores.add(jugador);
				copiamodelo.actualplayers++;
				System.out.println("Ha ingresado el primer jugador. Hay " + jugadores.size() + ".");
				enviarpartida(jugador);
				informardejugadores();
				Listados.informaraloshalls();
				if (jugadores.size() == numplayers) {
					onair = true;
				}
			} else {
				System.out.println("Denegando entrada al jugador.");
				try {jugador.salida.writeUTF("GETPARTIDA%DENEGADA");} catch (IOException e) {e.printStackTrace();}
			}
		
		}
		
		
		
	}



	private void generartablero() {
		
		tableromostrado = new boolean[cols][rows];
		tablero = new int[cols][rows];
		//Creo una lista de las celdas sin asignar
		ArrayList<String> celdasvacias = new ArrayList<>();
		for(int i = 0; i < cols; i++) {
			for(int j = 0; j < rows; j++) {
			celdasvacias.add(i+"%"+j);
		}	
		}
		//el numero de minas que habrá será un 7% de las casillas x la dificultad, siendo entre un 7% y un 35%
		minas = (int) Math.round(((double)cols* (double)rows* 0.07* (double)dificultad));
		if (cheating) {minas = 25;}
		minasrestantes = minas;
		//asigno las minas a celdas sin asignar aleatorias
		for(int i = 0; i < minas ; i++) {
			
			int indicecelda = (int)(Math.random()*(double) celdasvacias.size());
			String[] celdaarellenar = celdasvacias.get(indicecelda).split("%");
			celdasvacias.remove(indicecelda);
			tablero[Integer.parseInt(celdaarellenar[0])][Integer.parseInt(celdaarellenar[1])] = 10; //el valor 10 significa mina
		}
		

		
		//ahora hay que generar los números
		for(int col=0; col<cols; col++){
			for(int row=0; row<rows; row++){
				
				int contadordeminas = 0;
			if(tablero[col][row]!=10) { //si no es una mina
				
				
				try {if (tablero[col-1][row-1]==10) {contadordeminas++;}} catch (Exception e) {}
				try {if (tablero[col-1][row]==10) {contadordeminas++;}} catch (Exception e) {}
				try {if (tablero[col-1][row+1]==10) {contadordeminas++;}} catch (Exception e) {}
				try {if (tablero[col][row-1]==10) {contadordeminas++;}} catch (Exception e) {}
				try {if (tablero[col][row+1]==10) {contadordeminas++;}} catch (Exception e) {}
				try {if (tablero[col+1][row-1]==10) {contadordeminas++;}} catch (Exception e) {}
				try {if (tablero[col+1][row]==10) {contadordeminas++;}} catch (Exception e) {}
				try {if (tablero[col+1][row+1]==10) {contadordeminas++;}} catch (Exception e) {}
				
				tablero[col][row] = contadordeminas;
				
				
			}
		
		
			}
		}
		
		System.out.println("Tablero generado.");
		//para el system voy a usar el modelo row/col para que se muestren en orden
		for (int row = 0; row < rows; row++) {
			System.out.println();
			for (int col = 0; col < cols; col++) {
				System.out.print(tablero[col][row] + "\t");
			}
		}
		System.out.println();
		
	}


	//informa a los jugadores de que el número de jugadores ha cambiado
	private void informardejugadores() {
		
		String result = "GETJUGADORES";
		for (int i = 0; i < jugadores.size(); i++) {
			result += "%"+jugadores.get(i).nombre+"/"+jugadores.get(i).puntuacion;
		}
		for(int i = 0; i < jugadores.size(); i++) {
			
			try {
				System.out.println("Informando de los jugadores al jugador: "+(i+1));
				jugadores.get(i).salida.writeUTF(result);
			} catch (SocketException e) {

			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		if(minasrestantes == 0) {onair = false; finalizarpartida();}
		
	}


	public void enviarpartida(Jugador jugador) {
		
		System.out.println("Enviando datos de la partida...");
			try {
				jugador.salida.writeUTF("GETPARTIDA%"+nombre+"%"+cols+"%"+rows+"%"+dificultad+"%"+numplayers+"%"+minas);
			} catch (IOException e) {e.printStackTrace();}
			
		System.out.println("Datos enviados.");
		
	}


	public void setidpartida(int idpartida) {
		this.idpartida = idpartida;
	}


	public void pulsarmina(Jugador jugador, int col, int row) {
		
		if (onair && !tableromostrado[col][row]) {
			switch(tablero[col][row]) {
			
			case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8:
			informarcasilla(col, row, tablero[col][row]);
			tableromostrado[col][row] = true;
			jugador.puntuacion += 20*dificultad;
			break;
			case 0: despejar(col, row, jugador, true); break;
			case 10: informarcasilla(col, row, tablero[col][row]); jugador.puntuacion -= 100*dificultad; minasrestantes--; break;
			case 11: System.out.println("Error, no se puede pulsar en una bandera.");;
			
			} 
		} else {
			System.out.println("Error, la partida no está comenzada o esa casilla ya está pulsada.");
		}
		
		informardejugadores();
	}




	private void despejar(int col, int row, Jugador jugador, boolean clickizquierdo) {
		try {
			
		if (!tableromostrado[col][row] && tablero[col][row] == 0) {
			informarcasilla(col, row, tablero[col][row]);
			tableromostrado[col][row] = true;
			despejar(col-1, row-1, jugador, clickizquierdo);
			despejar(col-1, row, jugador, clickizquierdo);
			despejar(col-1, row+1, jugador, clickizquierdo);
			despejar(col, row-1, jugador, clickizquierdo);
			despejar(col, row, jugador, clickizquierdo);
			despejar(col, row+1, jugador, clickizquierdo);
			despejar(col+1, row-1, jugador, clickizquierdo);
			despejar(col+1, row, jugador, clickizquierdo);
			despejar(col+1, row+1, jugador, clickizquierdo);
			
			
		} else if (!tableromostrado[col][row]) {
			
			tableromostrado[col][row] = true;
			informarcasilla(col, row, tablero[col][row]);
			if(clickizquierdo) {jugador.puntuacion += 5*dificultad;} else {jugador.puntuacion -= 5*dificultad;}
			
			
		}
		}catch (ArrayIndexOutOfBoundsException e) {}
		
	}


	private void informarcasilla(int col, int row, int value) {
		
		for(int i=0; i < jugadores.size(); i++) {
		try {
			jugadores.get(i).salida.writeUTF("GETCASILLA%"+col+"%"+row+"%"+value);
	
		} catch (SocketException e) {
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		System.out.println("Jugadores informados de la apertura de casilla.");
	}


	public void pulsarbandera(Jugador jugador, int col, int row) {
		
		if (onair && !tableromostrado[col][row]) {
			switch(tablero[col][row]) {
			
			case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8:
			informarcasilla(col, row, tablero[col][row]);
			tableromostrado[col][row] = true;
			jugador.puntuacion -= 100*dificultad;
			break;
			case 0: despejar(col, row, jugador, false); break;
			case 10: tablero[col][row]=11; informarcasilla(col, row, 11); jugador.puntuacion += 100*dificultad; minasrestantes--; break;
			
			
			} 
		if(cheating) {jugador.puntuacion -= 5000;}
		} else {
			System.out.println("Error, la partida no está comenzada o esa casilla ya está pulsada.");
		}
		
		informardejugadores();
	}
	
	private void finalizarpartida() {
		
		for (int i = 0; i < jugadores.size(); i++) {
			
		
			try {
				
				HibernateSession.getTransaction();
				Usuarios usuario = (Usuarios) HibernateSession.session.createQuery("from Usuarios where idusuario = '"
						+ jugadores.get(i).id + "'").getSingleResult();
				usuario.setPuntos(usuario.getPuntos()+jugadores.get(i).puntuacion);
				jugadores.get(i).salida.writeUTF("GETELO%"+usuario.getPuntos()+"%"+jugadores.get(i).puntuacion);
				HibernateSession.commitclose();
				
				System.out.println("200");
				
			} catch (SocketException e) {
				HibernateSession.commitclose();

				System.out.println("404");
			} catch (NoResultException e) {
				HibernateSession.commitclose();

				System.out.println("404");
			} catch (Exception e) {
				HibernateSession.rollbackclose();
				e.printStackTrace();
				System.out.println("500");			
			}
		
		}
	
		
	}
	

	
}
