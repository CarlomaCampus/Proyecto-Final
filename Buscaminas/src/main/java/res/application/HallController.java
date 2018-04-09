package res.application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

import com.google.gson.Gson;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class HallController extends Application {

	@FXML
	Slider rows, cols, dificultad, numplayers;

	@FXML
	Button botoncrear;

	@FXML
	TextField nombrepartida;

	@FXML
	ImageView statusimage;

	Image tick, cross;

	@FXML
	Text statustext, usernametext, elotext;

	@SuppressWarnings("rawtypes")
	@FXML
	TableView tablapartidas;

	@SuppressWarnings("rawtypes")
	@FXML
	TableColumn nombrecol, jugadorescol, tablerocol, dificultadcol;

	Global conexion;
	Gson gson = new Gson();
	ObservableList<PartidasModelo> partidas;
	boolean close = false;
	boolean prueba = true;

	// Inicia la GUI aprovechando cualquier constructor existente.
	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/Hall.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		Scene scene = new Scene(pane);
		primaryStage.setTitle("Hall");
		primaryStage.getIcons().add(new Image("/res/application/res/mina.png"));
		primaryStage.setScene(scene);
		primaryStage.getScene().getStylesheets().add(getClass().getResource("/res/application.css").toExternalForm());
		primaryStage.show();
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				try {
					close = true;
					try {
						conexion.socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					} // Cierro la conexi�n del hall
				} catch (Exception e) {
				}
			}
		});

	}

	// Este m�todo se llama despu�s de start() y tiene acceso a los @FXML ya
	// inicializados
	@SuppressWarnings("unchecked")
	public void initialize() {

		Global.setToken(TokenUtils.obtenerTokenGuardado());
		// asigno a las columnas las variables de las partidas que est�n almacenadas en
		// la observable list y que
		// se encarga de actualizar la clase ListenerServerHall
		partidas = FXCollections.observableArrayList();
		// Vinculo la tabla con la Obbservable List
		tablapartidas.setItems(partidas);
		
		nombrecol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		jugadorescol.setCellValueFactory(new PropertyValueFactory<>("playercount"));
		tablerocol.setCellValueFactory(new PropertyValueFactory<>("tablero"));
		dificultadcol.setCellFactory(param -> {
		
		       final ImageView imageview = new ImageView();

		      
		       TableCell<PartidasModelo, Image> cell = new TableCell<PartidasModelo, Image>() {
		           public void updateItem(Image item, boolean empty) {
		        	   
		        	   if(item == null) {
		                    setGraphic(null);
		                } else {
		                    imageview.setImage(item);
		                    setGraphic(imageview);
		                }
		            
		           }
		        };
		        return cell;
		   });
		   dificultadcol.setCellValueFactory(new PropertyValueFactory<PartidasModelo, Image>("dificultadImage"));;

		tick = new Image("/res/application/res/tick.png");
		cross = new Image("/res/application/res/cross.png");
		tablapartidas.setPlaceholder(new Label("Todavía no hay partidas. ¡Crea la tuya!"));
		usernametext.setText(Global.info.split("%")[0]);
		elotext.setText(Global.info.split("%")[1]);
		
		// Establecemos la comunicacion
		try {
			conexion = new Global();
			conexion.socket = new Socket(Global.IP, 5003);
			statusimage.setImage(tick);
			statustext.setText("Estás conectado al servidor.");
			conexion.entrada = new DataInputStream(conexion.socket.getInputStream());
			conexion.salida = new DataOutputStream(conexion.socket.getOutputStream());
			conexion.salida.writeUTF("CONNECT%HALL%" + Global.getToken());

		} catch (ConnectException e) {
			statusimage.setImage(cross);
			statustext.setText("No se ha podido conectar con el servidor.");

		} catch (NullPointerException e) {
			statusimage.setImage(cross);
			statustext.setText("No tienes conexión con el servidor.");

		} catch (Exception e) {
			statusimage.setImage(cross);
			statustext.setText("Error inesperado al conectar con el servidor: " + e.toString());
			e.printStackTrace();
		}

		// A partir de ahora se ejecutar� la tarea en segundo plano para evitar que el
		// hilo de la GUI est� parado mientras
		// se escucha al servidor, y esta clase se seguir�
		// usando para actualizar la GUI.

		// listener = new ListenerServerHall(this);
		// hilo = new Thread(listener);
		// hilo.start();

		@SuppressWarnings("rawtypes")
		Task task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				Global conexion;

				conexion = new Global();
				try {

					conexion.socket = new Socket(Global.IP, 5003);
					conexion.entrada = new DataInputStream(conexion.socket.getInputStream());
					conexion.salida = new DataOutputStream(conexion.socket.getOutputStream());
					conexion.salida.writeUTF("CONNECT%HALLLISTENER%" + Global.getToken());

				} catch (ConnectException e) {
					statustext.setText("No se ha podido conectar con el servidor.");
				} catch (Exception e) {
					e.printStackTrace();
				}

				

				while (true) {
					// Optimiza el servidor, haciendo que le elimine de la lista de los halls
					// oyentes si se cierra la ventana del hall
					if (close) {
						conexion.socket.close();
						System.out.println("Cerrando.");
						break;
					}
					try {
						String[] responseparts = conexion.entrada.readUTF().split("%");
						switch (responseparts[0]) {
						case "GETPARTIDAS":
							actualizartabla(responseparts);
							break;
						case "401":
							Platform.runLater(new Runnable() {
							@Override
							public void run() {
								statusimage.setImage(cross);
								statustext.setText(
										"El servidor no te ha autorizado, ponte en contacto con el administrador: " + responseparts[0]);
							}
						});
						break;
						default:
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									statusimage.setImage(cross);
									statustext.setText(
											"No se ha entendido la respuesta del servidor: " + responseparts[0]);
								}
							});
							break;
						}
						
					} catch (SocketException e) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								statusimage.setImage(cross);
								statustext.setText(
										"Te has desconectado del servidor. Es posible que esté en mantenimiento, por favor, reinicia la aplicación.");
							}
						});
						break;

					} catch (IOException e) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								statusimage.setImage(cross);
								statustext.setText(
										"Ha habido un problema con la comunicación con el servidor: " + e.toString());
							}
						});
						break;
					} catch (NullPointerException e) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								statusimage.setImage(cross);
								statustext.setText(
										"No tienes conexión con el servidor. Reinicia la aplicación, por favor.");
							}
						});
						break;
					} catch (Exception e) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								statusimage.setImage(cross);
								statustext.setText("Error inesperado: " + e.toString());
							}
						});
						e.printStackTrace();
						break;
					}

				}

				conexion.socket.close();

				return null;

			}

			private void actualizartabla(String[] responseparts) {

				String json = "";
				// pudiendo haber m�s guiones en el json, vamos a juntar en un solo sring todo
				// lo que no sea la posici�n 1
				for (int i = 1; i < responseparts.length; i++) {
					json += responseparts[i];
				}
				// y ahora convertimos el json en un array, para poder actualizar el observable
				// list
			

				PartidasModelo[] partidasarray = gson.fromJson(json, PartidasModelo[].class);
				partidas.clear();
				for (int i = 0; i < partidasarray.length; i++) {
					
					
					switch(partidasarray[i].dificultad) {
					
					case 1: partidasarray[i].setDificultadImage(new Image("/res/application/res/dif1.png")); break;
					case 2: partidasarray[i].setDificultadImage(new Image("/res/application/res/dif2.png")); break;
					case 3: partidasarray[i].setDificultadImage(new Image("/res/application/res/dif3.png")); break;
					case 4: partidasarray[i].setDificultadImage(new Image("/res/application/res/dif4.png")); break;
					case 5: partidasarray[i].setDificultadImage(new Image("/res/application/res/dif5.png")); break;
					
					}
					partidas.add(partidasarray[i]);
				}
				
				
					
				

				for (int i = 0; i < partidas.size(); i++) {
					if (partidas.get(i).getNumplayers() == partidas.get(i).actualplayers) {
						partidas.remove(i);
						i--;
					}
				}

			}
		};

		new Thread(task).start();

	}

	public void crearpartida() {
		// Creo una ventana de partida que se comunicar� con el socket mediante otra
		// conexi�n.
		// El hall le pasa los datos de la partida creada en un string, separado por
		// guiones para poder splitearlo.
		// Le paso como par�metro el id de la partida que se acaba de generar para que
		// se ingrese automaticamente.

		try {

			conexion.salida.writeUTF("CREATE%" + nombrepartida.getText().replaceAll("%", "-") + "%" + cols.getValue()
					+ "%" + rows.getValue() + "%" + dificultad.getValue() + "%" + numplayers.getValue());
			new PartidaController(Integer.parseInt(conexion.entrada.readUTF()), this).start(new Stage());
		} catch (NullPointerException e) {
			statusimage.setImage(cross);
			statustext.setText("No tienes conexión con el servidor.");

		} catch (Exception e) {
			statusimage.setImage(cross);
			statustext.setText("Error inesperado al conectar con el servidor: " + e.toString());
			e.printStackTrace();
		}

	}

	public void tablaclick() {

		try {
			PartidasModelo p = partidas.get(tablapartidas.getSelectionModel().getSelectedIndex());
			if (p.numplayers == p.actualplayers) {
				statusimage.setImage(cross);
				statustext.setText("La partida se acaba de llenar, lo siento.");
			} else {
				new PartidaController(p.getIdpartida(), this).start(new Stage());
			}
		} catch (NullPointerException e) {
			statusimage.setImage(cross);
			statustext.setText("No tienes conexión con el servidor.");
		} catch (IndexOutOfBoundsException e) {

		} catch (Exception e) {
			statusimage.setImage(cross);
			statustext.setText("Error inesperado al conectar con el servidor: " + e.toString());
			e.printStackTrace();
		}
	}
	
	public void cerrarsesionclick() {

		
		Global.saveCheckboxState(false);
		((Stage) botoncrear.getScene().getWindow()).close();
	}

}
