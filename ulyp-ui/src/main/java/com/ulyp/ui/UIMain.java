package com.ulyp.ui;

import com.ulyp.agent.transport.GrpcUiTransport;
import com.ulyp.ui.server.UIConnectorServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class UIMain extends Application {

    private PrimaryViewController viewController;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(UIMain.class.getClassLoader().getResource("PrimaryView.fxml"));
        Parent root = loader.load();

        this.viewController = loader.getController();

        FileChooser fileChooser = new FileChooser();

        this.viewController.fileChooser = () -> fileChooser.showOpenDialog(stage);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("modena.css");

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.setTitle("ULYP");
        InputStream iconStream = UIMain.class.getClassLoader().getResourceAsStream("icon.png");
        if (iconStream == null) {
            throw new RuntimeException("Icon not found");
        }
        stage.getIcons().add(new Image(iconStream));

        stage.show();

        startGrpcServer();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void startGrpcServer() throws IOException {
        Server server = ServerBuilder.forPort(GrpcUiTransport.DEFAULT_ADDRESS.port)
                .maxInboundMessageSize(1324 * 1024 * 1024)
                .addService(new UIConnectorServiceImpl(viewController))
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.shutdownNow();

                server.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }

        }));
    }
}
