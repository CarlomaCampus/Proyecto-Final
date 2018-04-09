package res.application;


public class Usuarios {

	int idusuario;
	String username;
	String token;
	String encriptedpassword;
	int elo;

	public Usuarios(String username, String encriptedpassword) {
		this.username = username;
		this.encriptedpassword = encriptedpassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEncriptedpassword() {
		return encriptedpassword;
	}

	public void setEncriptedpassword(String encriptedpassword) {
		this.encriptedpassword = encriptedpassword;
	}

	public int getElo() {
		return elo;
	}

	public void setElo(int elo) {
		this.elo = elo;
	}

	public int getIdusuario() {
		return idusuario;
	}

	public void setIdusuario(int idusuario) {
		this.idusuario = idusuario;
	}
}
