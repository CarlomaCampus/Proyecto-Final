package res.application;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class IPChangerController extends Application{

	@FXML
	Hyperlink contactolink, descargarlink;
	
	@FXML
	TextField ipfield;
	
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/IPChanger.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		Scene scene = new Scene(pane);
		primaryStage.setTitle("AYUDA");
		primaryStage.getIcons().add(new Image("/res/application/res/mina.png"));
		primaryStage.setScene(scene);
		primaryStage.getScene().getStylesheets()
				.add(getClass().getResource("/res/application.css").toExternalForm());
		primaryStage.show();
		primaryStage.setResizable(false);
	}

	
	public void initialize() {
		
		
		contactolink.setOnAction((ActionEvent e) -> {
			if (Desktop.isDesktopSupported()) {
			    try {
					Desktop.getDesktop().browse(new URI("https://chat.whatsapp.com/EREAqHFwG3THKjQQjySEU2"));
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		descargarlink.setOnAction((ActionEvent e) -> {
			if (Desktop.isDesktopSupported()) {
			    try {
					Desktop.getDesktop().browse(new URI("https://drive.google.com/file/d/1yNePShsYOcMEd9GuBy5YDQaLdbcyOqHu/view"));
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		
		
		
	}
	
	@FXML
	public void okaction() {
		
		
		TokenUtils.guardarIP(ipfield.getText());
		((Stage) ipfield.getScene().getWindow()).close();
		
		
	}

}
