package res.application;

import javafx.scene.image.Image;

public class PartidasModelo {
	 
	
	int idpartida;
	String nombre;
	int cols, rows;
	int dificultad;
	transient Image dificultadImage;
	transient boolean isimage;
	int numplayers = 0;
	String tablero;
	int actualplayers;
	String playercount;
	
	
	
	public PartidasModelo(int idpartida, String nombre, int cols, int rows, int dificultad, int numplayers) {
		this.idpartida = idpartida;
		this.nombre = nombre;
		this.cols = cols;
		this.rows = rows;
		this.numplayers = numplayers;
		this.tablero = cols + " x " + rows;
	}
	
	public PartidasModelo() {
		super();
	}
	public String getTablero() {
		return  cols + " x " + rows;
	}
	public void setTablero(String tablero) {
		this.tablero = cols + " x " + rows;
	}
	public int getIdpartida() {
		return idpartida;
	}
	public void setIdpartida(int idpartida) {
		this.idpartida = idpartida;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getCols() {
		return cols;
	}
	public void setCols(int cols) {
		this.cols = cols;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getDificultad() {
		return dificultad;
	}
	public void setDificultad(int dificultad) {
		this.dificultad = dificultad;
	}
	public int getNumplayers() {
		return numplayers;
	}
	public void setNumplayers(int numplayers) {
		this.numplayers = numplayers;
	}
	public int getActualplayers() {
		return actualplayers;
	}
	public void setActualplayers(int actualplayers) {
		this.actualplayers = actualplayers;
	}
	public String getPlayercount() {
		return actualplayers + " / " + numplayers;
	}
	public void setPlayercount(String playercount) {
		this.playercount = playercount = actualplayers + " / " + numplayers;
	}
	public Image getDificultadImage() {
		return dificultadImage;
	}
	public void setDificultadImage(Image dificultadImage) {
		this.dificultadImage = dificultadImage;
	}
	

}
