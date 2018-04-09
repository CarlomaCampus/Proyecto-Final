package res.application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PartidaController extends Application {

	@FXML
	private GridPane gridpane;

	@FXML
	private Pane pane;

	Image tick, cross;

	@FXML
	ImageView statusimage;

	@FXML
	Text statustext;

	@FXML
	ProgressBar playersbar;
	 
	@FXML
	Text estadotext, numplayerstext, diftext, minastext, flagstext;
	
	@FXML
	ImageView difico, minaico, flagico;
	
	@FXML
	GridPane puntuaciongrid;
	
	FinPartidaController fp = new FinPartidaController();
	HallController h;
	String nombrepartida;
	int cols, rows, dificultad, numplayers, minas;
	String info;
	boolean iserror = false;
	Global conexion;
	Button[][] botones;
	ObservableList<HBoxCell> players = FXCollections.observableArrayList();
	boolean denegada;


	public PartidaController(int idpartida, HallController h) {
		
		this.h = h;
		// Establecemos la comunicacion
		conexion = new Global();
		try {

			conexion.socket = new Socket(Global.IP, 5003);
			conexion.entrada = new DataInputStream(conexion.socket.getInputStream());
			conexion.salida = new DataOutputStream(conexion.socket.getOutputStream());

		} catch (ConnectException e) {
			iserror = true;
			info = "No se ha podido conectar con el servidor.";
		} catch (Exception e) {
			iserror = true;
			info = "Error inesperado. No se ha podido conectar con el servidor.";
			e.printStackTrace();
		}

		
		// CREAMOS LA PARTIDA
		try {
			conexion.salida.writeUTF("CONNECT%JUGADOR%"+Global.getToken());

		} catch (NullPointerException e) {
			iserror = true;
			info = "No tienes conexión con el servidor de modo que no se ha podido crear la partida.";

		} catch (Exception e) {
			e.printStackTrace();
			iserror = true;
			info = "Error inesperado: " + e.toString();
		}

		
		
		try {
			String[] responseparts = null;
			
			conexion.salida.writeUTF("GETPARTIDA%"+idpartida);
			responseparts = conexion.entrada.readUTF().split("%");
			if (responseparts[0].equals("GETPARTIDA") && !responseparts[1].equals("DENEGADA")) {

				nombrepartida = responseparts[1];
				cols = Integer.parseInt(responseparts[2]);
				rows = Integer.parseInt(responseparts[3]);
				dificultad = Integer.parseInt(responseparts[4]);
				numplayers = Integer.parseInt(responseparts[5]);
				minas = Integer.parseInt(responseparts[6]);

				iserror = false;
				info = "Partida generada, esperando jugadores..."; // estos valores se usan en el initialize, que aqu�
																	// no se
																	// puede
			} else if (responseparts[0].equals("401")) {
				iserror = true;
				info = "El servidor no te ha autorizado, ponte en contacto con el administrador.";
			} else if (responseparts[1].equals("DENEGADA")) {
				iserror = true;
				info = "No puedes entrar dos veces en la misma partida.";
				denegada = true;
			} else {

				iserror = true;
				info = "Ha habido un error en la comunicación, se esperaba un GETPARTIDA pero el servidor ha enviado un "+responseparts[0];
			}

			
		} catch (NullPointerException e) {
			iserror = true;
			info = "No tienes conexión con el servidor, por favor, reinicia la aplicación.";

		} catch (Exception e) {
			e.printStackTrace();
			iserror = true;
			info = "Ha habido un error en la conexión: " + e.toString();
		}
	}

	// Inicia la GUI aprovechando cualquier constructor existente.
	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/Partida.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		Scene scene = new Scene(pane);
		primaryStage.setTitle(nombrepartida);
		primaryStage.setScene(scene);
		primaryStage.getScene().getStylesheets().add(getClass().getResource("/res/application.css").toExternalForm());
		primaryStage.show();
		primaryStage.getIcons().add(new Image("/res/application/res/mina.png"));
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    public void handle(WindowEvent event) {
		      try {conexion.socket.close();} catch (Exception e) {e.printStackTrace();}
		    }
		});

	}

	// Este m�todo se llama despu�s de start() y tiene acceso a los @FXML ya
	// inicializados, har� un setup b�sico de los elementos y finalmente llamar� al Listener del Server para que lo actualice.
	
	public void initialize() {

		if (denegada) {
			playersbar.setVisible(false); numplayerstext.setVisible(false); minastext.setVisible(false); flagstext.setVisible(false);
			diftext.setVisible(false); difico.setVisible(false); minaico.setVisible(false); flagico.setVisible(false);
			estadotext.setText("No puedes entrar varias veces en la misma partida con la misma cuenta.");
		}
		
		tick = new Image("/res/application/res/tick.png");
		cross = new Image("/res/application/res/cross.png");
		
		if (iserror) {
			statusimage.setImage(cross);
		} else {
			statusimage.setImage(tick);
		}

		statustext.setText(info);

		ColumnConstraints column = new ColumnConstraints();
		column.setMaxWidth(Double.NEGATIVE_INFINITY);
		column.setMinWidth(Double.NEGATIVE_INFINITY);

		RowConstraints row = new RowConstraints();
		row.setMinHeight(Double.NEGATIVE_INFINITY);
		numplayerstext.setText("(0/ " + numplayers + ")");
		diftext.setText(dificultad + "");
		minastext.setText(minas+"");
		flagstext.setText(minas+"");

		botones = new Button[cols][rows];
		for (int i = 0; i < cols; i++) {
			gridpane.getColumnConstraints().add(column);
			gridpane.getRowConstraints().add(row);
			for (int j = 0; j < rows; j++) {
				final BotonTablero boton = new BotonTablero(i, j);
				boton.getStyleClass().add("botontablero");
				// Creo botones con listeners que conocen su lugar y quien son
				// No escucha cuando es pulsado, sino cuando se clicka el rat�n encima de �l y entonces se decide qu� hacer seg�n
				// el bot�n del mouse que se haya pulsado
				boton.setOnMouseClicked(new EventHandler<MouseEvent>() {

		            public void handle(MouseEvent event) {
		                MouseButton button = event.getButton();
		                if(button==MouseButton.PRIMARY){
		                	try {
		        				conexion.salida.writeUTF("PULSARMINA%"+boton.col+"%"+boton.row);
		        			} catch (NullPointerException e) {
		        				statusimage.setImage(cross);
		        				statustext.setText("No tienes conexión con el servidor.");
		        			
		        			} catch (Exception e) {
		        				statusimage.setImage(cross);
		        				statustext.setText("Error inesperado al conectar con el servidor: "+e.toString());
		        				e.printStackTrace();
		        			}
		        			
		                }else if(button==MouseButton.SECONDARY){
		                	try {
		        				conexion.salida.writeUTF("PULSARBANDERA%"+boton.col+"%"+boton.row);
		        			} catch (NullPointerException e) {
		        				statusimage.setImage(cross);
		        				statustext.setText("No tienes conexión con el servidor.");
		        			
		        			} catch (Exception e) {
		        				
		        				statusimage.setImage(cross);
		        				statustext.setText("Error inesperado al conectar con el servidor: "+e.toString());
		        				e.printStackTrace();
		        			}
		                }else if(button==MouseButton.MIDDLE){
		                	 statustext.setText("Easter egg provisional: Interrogante sin implementar.");
		                }
		            }
		        });
				boton.setPrefSize(25, 25);
				gridpane.add(boton, i, j);
				botones[i][j] = boton;

			}
		}

		gridpane.setMaxWidth(Double.NEGATIVE_INFINITY);
		gridpane.setMinWidth(Double.NEGATIVE_INFINITY);
		puntuaciongrid.setHgap(10);
		puntuaciongrid.setVgap(10);
		
		
		@SuppressWarnings("rawtypes")
		Task task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				while (true) {
					try {
						String[] responseparts = conexion.entrada.readUTF().split("%");
						switch (responseparts[0]) {
						case "GETPARTIDA":
							Platform.runLater(new Runnable() { public void run() {
									statusimage.setImage(cross);
									statustext.setText("El servidor te está enviando información que ya tienes.");
							}});
							break;
						case "GETJUGADORES": getJugadores(responseparts); break;
						case "GETCASILLA": getcasilla(responseparts); break;
						case "GETELO": getelo(responseparts); break;}

					} catch (IOException e) {
						statusimage.setImage(cross);
						statustext
								.setText("Ha habido un problema con la comunicación con el servidor: " + e.toString());
						break;
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}

				}
				// }});
				return null;

			}

		     private void getcasilla(final String[] responseparts) {
		    	 
		    	 Platform.runLater(new Runnable() { @Override public void run() {
						Label l = null;
						if(Integer.parseInt(responseparts[3]) == 0) { } else { l = new Label(responseparts[3]); l.getStyleClass().add("bordegris");}
						
						switch(responseparts[3]) {
							case "0": l = new Label(); break;
							case "1": l.getStyleClass().add("colorone"); break;
							case "2": l.getStyleClass().add("colortwo"); break;
							case "3": l.getStyleClass().add("colorthree"); break;
							case "4": l.getStyleClass().add("colorfour"); break;
							case "5": l.getStyleClass().add("colorfive"); break;
							case "6": l.getStyleClass().add("colorsix"); break;
							case "7": l.getStyleClass().add("colorseven"); break;
							case "8": l.getStyleClass().add("coloreight"); break;
							case "10": l = new Label();
								ImageView i = new ImageView("/res/application/res/mina.png");
								i.setFitWidth((botones[0][0].getWidth()));
								i.setFitHeight((botones[0][0].getHeight()));
								l.setGraphic(i);
								flagstext.setText(Integer.parseInt(flagstext.getText())-1+"");
							break;
							case "11": l = new Label();
								ImageView j = new ImageView("/res/application/res/flag.png");
								j.setFitWidth((botones[0][0].getWidth()));
								j.setFitHeight((botones[0][0].getHeight()));
								l.setGraphic(j);
								flagstext.setText(Integer.parseInt(flagstext.getText())-1+"");
								
						break;
							
							}
						if(Integer.parseInt(flagstext.getText()) == 0) {
						finalizarpartida();}
						
							l.getStyleClass().add("textcasilla");
							l.prefWidthProperty().bind(botones[0][0].prefWidthProperty());
							l.prefHeightProperty().bind(botones[0][0].prefWidthProperty());
							l.setAlignment(Pos.CENTER);
							l.setTextAlignment(TextAlignment.CENTER);
							gridpane.add(l, Integer.parseInt(responseparts[1]),
									Integer.parseInt(responseparts[2]));
							botones[Integer.parseInt(responseparts[1])][Integer.parseInt(responseparts[2])].setVisible(false);
							}});
							
			}

			public void getJugadores(final String[] responseparts) {
		    	 
		    	 Platform.runLater(new Runnable() { // Necesario para que JavaFX controle los hilos y no salte el bug de tener el mismo elemento de la GUI en varios hilos (o en varias ejecuciones).
	                 @Override public void run() {
	                	 
	                	                	 
	                	 
	                	if (responseparts.length-1 == numplayers) {
	                	gridpane.setDisable(false);
	                	estadotext.setText("Partida en curso...");
	                	numplayerstext.setText("");
						playersbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
						statustext.setText("Partida comenzada.");
	                	 } else {
	                	numplayerstext.setText("("+(responseparts.length-1)+" / "+numplayers+")");
						playersbar.setProgress((double)(responseparts.length-1)/(double)numplayers);
	                	}
	                	 
	                	 players.clear();
	                	 puntuaciongrid.getChildren().clear();
	                	 puntuaciongrid.add(new HBoxCell("Nombre del jugador", "jugadorcab"), 0, 0);
	             		 puntuaciongrid.add(new HBoxCell("Puntuación", "puntuacioncab"), 1, 0);
	                	 for (int i = 1; i < responseparts.length; i++) {
	                		puntuaciongrid.add(new HBoxCell(responseparts[i].split("/")[0], "jugador"), 0, i);
	                		puntuaciongrid.add(new HBoxCell(responseparts[i].split("/")[1], "puntuacion"), 1, i);
	    		            
	    		        }
	                	 
	                	
	                 }});	

		    	 
		        
		    	 
		     }
		     
		 };
		 
		 new Thread(task).start();
	}
	
	protected void getelo(String[] responseparts) {
		h.elotext.setText(responseparts[1]);
		fp.setpuntos(responseparts[2]);
		
	}
	
	private void finalizarpartida() {
		
		((Stage) flagstext.getScene().getWindow()).close();
		try {
			fp.start(new Stage());
		} catch (Exception e) {e.printStackTrace();}
		
		
	}
		public class HBoxCell extends HBox {

			Label label = new Label();
			Button button = new Button();

			HBoxCell(String text, String style) {
				super();
				label.setText(text);
				label.getStyleClass().add(style);
				label.setMaxWidth(Double.MAX_VALUE);
				HBox.setHgrow(label, Priority.ALWAYS);
				this.getChildren().addAll(label);
			}
		}
	
	

}
