package res.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Date;

public class TokenUtils {
	
	private static final String TOKEN_TXT_PATH = "src/main/java/res/application/token.txt";
	public static final String INFO_TXT_PATH = "src/main/java/res/application/info.txt";
	public static final String IP_TXT_PATH = "src/main/java/res/application/IP.txt";

	
	
	public static boolean elTokenExiste(){
		try {
        File file = new File(TOKEN_TXT_PATH);
        @SuppressWarnings("resource")
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        
			if (bufferedReader.readLine() == null) {
			    return false;
			} else {
			    return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
    }


    public static void guardarToken(String token) {

        try {
            File file = new File(TOKEN_TXT_PATH);
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(token);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public static void guardarIP(String ip) {

        try {
            File file = new File(IP_TXT_PATH);
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(ip);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Global.IP = ip;
        
    }
    
    public static void guardarInfo(String username, String puntos) {

        try {
            File file = new File(INFO_TXT_PATH);
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(username+"%"+puntos);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public static String obtenerDatosGuardados(String path) {
    	String linea;
    	File file = new File(path);
        BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
		
        
        linea = bufferedReader.readLine();
        bufferedReader.close();
        return linea;
		} catch (IOException e) { e.printStackTrace(); }
       return "";
    }

    
    public static boolean elTokenHaExpirado(String token) {

    	
        String[] parts = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String decoderPayload = new String(decoder.decode(parts[1]));
        String save = decoderPayload.substring(1, decoderPayload.length()-1);
        String [] tokenParts = save.split(",");
        String [] removedExp = tokenParts[1].split(":");
        int tokenExpDate = Integer.parseInt(removedExp[1]);

        if (tokenExpDate <= fechaActualConvertida()) {
            return true;
        } else {
            return false;
        }

    }
    
  public static boolean elTokenGuardadoHaExpirado() throws Exception {

    	String token = obtenerTokenGuardado();
        String[] parts = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String decoderPayload = new String(decoder.decode(parts[1]));
        String save = decoderPayload.substring(1, decoderPayload.length()-1);
        String [] tokenParts = save.split(",");
        String [] removedExp = tokenParts[1].split(":");
        int tokenExpDate = Integer.parseInt(removedExp[1]);

        if (tokenExpDate <= fechaActualConvertida()) {
            return true;
        } else {
            return false;
        }

    }

    public static int fechaActualConvertida() {
        Date now = new Date();
        Long longTime = now.getTime() / 1000;
        return longTime.intValue();
    }

    
    public static String obtenerTokenGuardado() {
    	String linea;
    	File file = new File(TOKEN_TXT_PATH);
        BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
		
        
        linea = bufferedReader.readLine();
        bufferedReader.close();
        return linea;
		} catch (IOException e) { e.printStackTrace(); }
       return "";
    }
    
}
