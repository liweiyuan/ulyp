package com.ulyp.ui;

import com.ulyp.agent.transport.UploadingTransport;
import com.ulyp.ui.server.UploadingServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class UIMain extends Application {

    private FXMLStackTraceViewController viewController;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Thread.currentThread().getContextClassLoader().getResource("FXMLStackTraceView.fxml"));
        Parent root = loader.load();

        this.viewController = loader.getController();

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.show();

        startStackTracesProcessing();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void startStackTracesProcessing() throws IOException {
        /*
        context.getConnectionAcceptExecutor().submit(
                new StackTraceListener(
                        context.getRequestProcessingExecutor(),
                        stackTrace -> {
                            MethodTree tree = MethodTreeUtils.from(stackTrace);
                            long hash = tree.hashCode();
                            viewController.onStackTraceUploaded(stackTrace, tree, hash);
                        }
                )
        );
        */

        Server server = ServerBuilder.forPort(UploadingTransport.DEFAULT_ADDRESS.port)
                .addService(new UploadingServiceImpl(r -> viewController.onMethodTraceTreeUploaded(r)))
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    server.shutdownNow();

                    server.awaitTermination(1, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }

            }
        });
    }
}
