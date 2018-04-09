package res.application;

import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RegistroController extends Application{

	Global conexion;
	String result = "";
	@FXML
	Text info;
	
	@FXML
	TextField usernamefield;
	
	@FXML
	PasswordField passfield, reppassfield;
	
	@FXML
	Hyperlink ayudalink;
	
	@FXML
	ProgressIndicator progress;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/Registro.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		Scene scene = new Scene(pane);
		primaryStage.setTitle("Registro");
		primaryStage.getIcons().add(new Image("/res/application/res/mina.png"));
		primaryStage.setScene(scene);
		primaryStage.getScene().getStylesheets()
				.add(getClass().getResource("/res/application.css").toExternalForm());
		primaryStage.show();
		primaryStage.setResizable(false);
	}

	
	public void registraraction() {
		
		ayudalink.setVisible(false);
		info.setVisible(false);
		final RegisterService servicelogin = new RegisterService(usernamefield, passfield, reppassfield, info);
	
		// mientras el servicelogin está ejecutando, la propiedad visible del
		// progressindicator ser� true
		progress.visibleProperty().bind(servicelogin.runningProperty());
		
		servicelogin.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent workerStateEvent) {
				// El login devuelve un c�digo para usarlo desde este thread
				result = servicelogin.getValue();
				String [] responseparts = result.split("%");
				
				switch (responseparts[0]) {

				case "200": info.setVisible(true); info.setText("Registro satisfactorio, ¡ya tienes una cuenta de Buscaminas Online!");break;
				case "400": info.setVisible(true); ayudalink.setVisible(true); info.setText("El servidor no responde."); break;
				case "401": info.setVisible(true); info.setText(result.split("%")[1]); break;	
				default: 
					info.setText("Ha habido un problema: "+result);
    				info.setVisible(true);
					break;
				
				}
				

			}
			
			
		});

		servicelogin.restart(); // here you start your service

		
		
	}
	
	@FXML
	private void linkaction() {
		
		try {new IPChangerController().start(new Stage());} catch (Exception e) {e.printStackTrace();}
		
	}
	

}
