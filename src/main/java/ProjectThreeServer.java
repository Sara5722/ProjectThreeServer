import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProjectThreeServer extends Application {
    private ServerSocket serverSocket;
    private boolean isRunning;
    private int port;
    private List<ClientHandler> clientHandlers;
    private ServerController serverController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/server.fxml"));
        Parent root = loader.load();
        serverController = loader.getController();
        serverController.setMainApp(this);

        clientHandlers = new CopyOnWriteArrayList<>();

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("3-Card Poker Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void startServer(int port) {
        this.port = port;
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                isRunning = true;

                Platform.runLater(() -> {
                    serverController.logMessage("Server started on port " + port);
                    serverController.updateClientCount(clientHandlers.size());
                });

                while (isRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        ClientHandler clientHandler = new ClientHandler(clientSocket, this, clientHandlers.size() + 1);
                        clientHandlers.add(clientHandler);
                        new Thread(clientHandler).start();

                        Platform.runLater(() -> {
                            serverController.logMessage("Client #" + clientHandler.getPlayerId() + " connected");
                            serverController.updateClientCount(clientHandlers.size());
                        });

                    } catch (IOException e) {
                        if (isRunning) {
                            Platform.runLater(() ->
                                    serverController.logMessage("Error accepting client: " + e.getMessage())
                            );
                        }
                    }
                }
            } catch (IOException e) {
                Platform.runLater(() ->
                        serverController.logMessage("Failed to start server: " + e.getMessage())
                );
            }
        }).start();
    }

    public void stopServer() {
        isRunning = false;

        // Close all client connections
        for (ClientHandler handler : clientHandlers) {
            handler.closeConnection();
        }
        clientHandlers.clear();

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }

        Platform.runLater(() -> {
            serverController.logMessage("Server stopped");
            serverController.updateClientCount(0);
        });
    }

    public void removeClientHandler(ClientHandler handler) {
        clientHandlers.remove(handler);
        Platform.runLater(() -> {
            serverController.logMessage("Client #" + handler.getPlayerId() + " disconnected");
            serverController.updateClientCount(clientHandlers.size());
        });
    }

    public void logGameEvent(String event) {
        Platform.runLater(() -> serverController.logMessage(event));
    }

    public static void main(String[] args) {
        launch(args);
    }
}