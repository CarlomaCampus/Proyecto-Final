package res.application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;

public class Global {

	//Esta clase se utiliza para usar la conexi�n con variables accesibles desde el Hall y desde la partida.
	
	Socket socket;
	DataInputStream entrada;
	DataOutputStream salida;
	private static String token;
	public static String info;
	public static String IP;
	public static String CHECKBOX_PATH = "src/main/java/res/application/checkbox.txt";
	public static PublicKey pub;
	
	
	
	public static String getToken() {
		return token;
	}
	
	public static void setToken(String token) {
				Global.token = token;
	}
	
	public static void saveCheckboxState (boolean state){
		
		try {FileWriter filewriter = new FileWriter(CHECKBOX_PATH); 
		PrintWriter printwriter = new PrintWriter(filewriter);
		if(state) {printwriter.println(true);}
		else {printwriter.println(false);}
		printwriter.close();
		} catch (IOException e) { e.printStackTrace();}
	}

	
}
