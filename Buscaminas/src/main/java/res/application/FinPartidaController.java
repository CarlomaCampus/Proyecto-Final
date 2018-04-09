package res.application;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FinPartidaController extends Application{

	String puntosdefault = "???";
	
	@FXML
	Text puntostext;
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/FinPartida.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		Scene scene = new Scene(pane);
		primaryStage.setTitle("FIN");
		primaryStage.getIcons().add(new Image("/res/application/res/mina.png"));
		primaryStage.setScene(scene);
		primaryStage.getScene().getStylesheets()
				.add(getClass().getResource("/res/application.css").toExternalForm());
		primaryStage.show();
		primaryStage.setResizable(false);
	}

	public void initialize() {
		
		puntostext.setText(puntosdefault);
		
	}
	
	public void setpuntos(String puntos) {
		
		puntostext.setText(puntos);
		
		
	};
	

}
