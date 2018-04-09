
public class PartidaModelo {

	
	int idpartida;
	String nombre;
	int cols, rows;
	int dificultad;
	int numplayers;
	int actualplayers;
	
	

	public PartidaModelo(int idpartida, String nombre, int cols, int rows, int dificultad, int numplayers) {

		this.idpartida = idpartida;
		this.nombre = nombre;
		this.cols = cols;
		this.rows = rows;
		this.dificultad = dificultad;
		this.numplayers = numplayers;
		this.actualplayers = 0;
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

	

}
