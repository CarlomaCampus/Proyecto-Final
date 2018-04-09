package res.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	static String a;
	
	public static void main(String[] args) {
		launch(args);
		
	}

	@Override
	public void start(Stage arg0) throws Exception {
		// Si la �ltima vez pulsaste recu�rdama, el token existe, y no ha expirado
		Global.IP = (TokenUtils.obtenerDatosGuardados(TokenUtils.IP_TXT_PATH));
		if(checkCheckboxState() && TokenUtils.elTokenExiste() && !TokenUtils.elTokenHaExpirado(TokenUtils.obtenerTokenGuardado())) {
			Global.setToken(TokenUtils.obtenerTokenGuardado());
			Global.info = TokenUtils.obtenerDatosGuardados(TokenUtils.INFO_TXT_PATH);
			new HallController().start(new Stage());
    		
    	} else {
		new LoginController().start(new Stage());
    	}
	}
	
	private static boolean checkCheckboxState(){

		File file = new File(Global.CHECKBOX_PATH);
		BufferedReader bufferedreader;

		try {
			bufferedreader = new BufferedReader(new FileReader(file));

			String line = bufferedreader.readLine();
			bufferedreader.close();
			if (line.equals("true")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	};

}
