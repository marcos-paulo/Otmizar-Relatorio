package com.marcos.relatorio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.marcos.layoutfactory.LayoutFactory;
import com.marcos.preloader.notification.InitNotification;
import com.marcos.preloader.notification.MessageNotification;
import com.marcos.preloader.util.Config;
import com.marcos.relatorio.controller.InicialController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader.ProgressNotification;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@SpringBootApplication
public class Main extends Application {

	private Contexto contexto;
	private LayoutFactory layoutFactory;
	private Image image;
	private Stage stage;
	
	private static final String URL_IMAGE = Main.class.getResource("images/OtimizarRelatorio.png").toExternalForm();
	
	public static int i = 0;
	public static int max = 21;

	public static void main(String[] args) {
		launch(Main.class, Config.args(args, "OtimizarRelatorio", "1.0.3", true, Main.URL_IMAGE));
	}
	
	@Override
	public void init() throws Exception {
		longInit();
	}

	private void longInit() {

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				notificarPreLoader("Iniciando modulo spring");
				/**
				 * Pegando uma instancia de ConfigurableApplicationContext 
				 * */
				ConfigurableApplicationContext springContext = SpringApplication.run(Main.class);
				springContext.registerShutdownHook();
								
				/**
				 * Instanciando o contexto que poderá ser 
				 * injetado em outras partes da aplicação
				 */
				contexto = springContext.getBean(Contexto.class);
				contexto.setSpringContext(springContext);
				
                notificarPreLoader("Carregando o Layout");
                carregarLayout();
                notificarPreLoader();
                                
                /**
                 * seta o primaryStage e a image no contexto principal
                 * para ser utilizado em outras partes da aplicação
                 */
                contexto.setPrimaryStage(stage);
                contexto.setImage(image);
                
                /**
                 * Enviando o Stage para o preLoader para ser executado
                 * após o carregamento dos componentes da aplicação.
                 */
				notifyPreloader(new InitNotification(stage));
				return null;
			}
		};
		new Thread(task).start();
	}
	
	private void carregarLayout(){
		try {
			layoutFactory = new LayoutFactory(contexto.getControllerFactory(), InicialController.class);
			contexto.setScene(layoutFactory.getScene());
			Platform.runLater(() -> {
				stage.setScene(contexto.getScene());
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void start(Stage primaryStage) {
		this.stage = primaryStage;
		image = new Image(URL_IMAGE);
		primaryStage.setMaximized(false);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Otimizar Relatório");
		primaryStage.getIcons().add(image);
		/** o método show erá invocado pelo preloader
		 *  depois de carregar todos os componentes da aplicação. */
//		primaryStage.show();
	}
	

    public void notificarPreLoader(String mensagen){
    	notifyPreloader(new MessageNotification(mensagen));
    }
    
    public int notificarPreLoader() {
		System.out.println("################################### Notificar Preloader");
		System.out.println("notificar Preloader " + ++i);
		notifyPreloader(new ProgressNotification(((double) i)/max));
    	return i;
    }

    @Override
    public void stop() throws Exception {
    	System.out.println("################################### metodo stop");
    	contexto.getSpringContext().stop();
    }

}
