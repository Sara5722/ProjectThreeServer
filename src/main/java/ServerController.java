import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ServerController {
    @FXML private TextField portField;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private TextArea logTextArea;
    @FXML private Label clientCountLabel;

    private ProjectThreeServer mainApp;

    public void initialize() {
        portField.setText("5555");
        stopButton.setDisable(true);
    }

    @FXML
    private void handleStartButton() {
        try {
            int port = Integer.parseInt(portField.getText());
            startButton.setDisable(true);
            stopButton.setDisable(false);
            portField.setDisable(true);
            mainApp.startServer(port);
        } catch (NumberFormatException e) {
            logMessage("Invalid port number");
        }
    }

    @FXML
    private void handleStopButton() {
        startButton.setDisable(false);
        stopButton.setDisable(true);
        portField.setDisable(false);
        mainApp.stopServer();
    }

    public void setMainApp(ProjectThreeServer mainApp) {
        this.mainApp = mainApp;
    }

    public void logMessage(String message) {
        logTextArea.appendText(message + "\n");
    }

    public void updateClientCount(int count) {
        clientCountLabel.setText("Connected Clients: " + count);
    }
}