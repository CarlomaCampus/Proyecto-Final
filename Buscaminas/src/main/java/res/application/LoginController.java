package res.application;

import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController extends Application {

	String result = "";


	@FXML
	private TextField username;

	@FXML
	private PasswordField password;

	@FXML
	private CheckBox rememberme;

	@FXML
	private Button enter;

	@FXML
	private Text info;
	
	@FXML
	private Hyperlink iplink;

	@FXML
	private ProgressIndicator progress;
	
	@FXML
	private Pane pane;

	@FXML
	private void enter() {

		final LoginService servicelogin = new LoginService(username, password, info);
		iplink.setVisible(false);
		info.setVisible(false);
		// mientras el servicelogin est� ejecutando, la propiedad visible del
		// progressindicator ser� true
		progress.visibleProperty().bind(servicelogin.runningProperty());
		
		servicelogin.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent workerStateEvent) {
				// El login devuelve un c�digo para usarlo desde este thread
				result = servicelogin.getValue();
				//String [] responseparts = result.split("%");
				
				switch (result) {

				case "200":
					Global.saveCheckboxState(rememberme.isSelected());
					((Stage) username.getScene().getWindow()).close();
					try {new HallController().start(new Stage());} catch (Exception e) {e.printStackTrace();}
					break;
					
				case "400":
					
					info.setText("No se ha podido establecer conexión con el servidor.");
					iplink.setVisible(true);
    				info.setVisible(true);
    				break;	
					
				case "401":
					
					info.setText("El usuario o la contraseña no son correctos.");
					iplink.setVisible(false);
    				info.setVisible(true);
    				break;	
    				
				default: 
					info.setText("El servidor no responde.");
					iplink.setVisible(true);
    				info.setVisible(true);
					break;
				
				}
				

			}
			
			
		});

		servicelogin.restart(); // here you start your service

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

	 	FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/Login.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        primaryStage.setTitle("Login");
        primaryStage.getIcons().add(new Image("/res/application/res/mina.png"));
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().add(getClass().getResource("/res/application.css").toExternalForm());
        primaryStage.show();
        primaryStage.setResizable(false);
       
        
	}

	
	
	@FXML
	private void iplinkaction() {
		
		try {new IPChangerController().start(new Stage());} catch (Exception e) {e.printStackTrace();}
		
	}
	
	@FXML
	private void registrarseaction() {
		
		try {new RegistroController().start(new Stage());} catch (Exception e) {e.printStackTrace();}
		
	}
	
}
