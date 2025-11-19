import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ProjectThreeServer server;
    private int playerId;
    private boolean running;
    private PokerGame currentGame;

    public ClientHandler(Socket socket, ProjectThreeServer server, int playerId) {
        this.clientSocket = socket;
        this.server = server;
        this.playerId = playerId;
        this.running = true;

        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error creating streams: " + e.getMessage());
            closeConnection();
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                PokerInfo clientInfo = (PokerInfo) in.readObject();
                handleClientMessage(clientInfo);
            }
        } catch (IOException | ClassNotFoundException e) {
            if (running) {
                System.err.println("Client #" + playerId + " disconnected: " + e.getMessage());
            }
        } finally {
            closeConnection();
        }
    }

    private void handleClientMessage(PokerInfo info) {
        switch (info.getMessageType()) {
            case "PLACE_BETS":
                processBet(info);
                break;
            case "PLAY":
                processPlay(info);
                break;
            case "FOLD":
                processFold();
                break;
        }
    }

    private void processBet(PokerInfo clientInfo) {
        currentGame = new PokerGame();
        currentGame.placeBets(clientInfo.getAnteBet(), clientInfo.getPairPlusBet());
        currentGame.dealHands();

        PokerInfo response = new PokerInfo("DEAL_CARDS");
        response.setPlayerHand(currentGame.getPlayerHand());
        response.setDealerHand(currentGame.getDealerHand());
        response.setSuccess(true);

        sendPokerInfo(response);

        server.logGameEvent("Client #" + playerId + " placed bets: Ante $" +
                clientInfo.getAnteBet() + ", Pair Plus $" + clientInfo.getPairPlusBet());
    }

    private void processPlay(PokerInfo clientInfo) {
        if (currentGame != null) {
            currentGame.makePlayWager();

            // Show dealer cards
            PokerInfo showDealer = new PokerInfo("SHOW_DEALER");
            showDealer.setDealerHand(currentGame.getDealerHand());
            sendPokerInfo(showDealer);

            // Calculate and send results
            PokerInfo result = currentGame.calculateResult();
            sendPokerInfo(result);

            server.logGameEvent("Client #" + playerId + " played. Result: " +
                    result.getGameMessage() + " Winnings: $" + result.getTotalWinnings());
        }
    }

    private void processFold() {
        if (currentGame != null) {
            int totalLoss = currentGame.getAnteBet() + currentGame.getPairPlusBet();

            PokerInfo result = new PokerInfo("GAME_RESULT");
            result.setGameMessage("You folded and lost your bets.");
            result.setTotalWinnings(-totalLoss);
            sendPokerInfo(result);

            server.logGameEvent("Client #" + playerId + " folded. Lost: $" + totalLoss);
        }
    }

    public void sendPokerInfo(PokerInfo info) {
        try {
            out.writeObject(info);
            out.flush();
        } catch (IOException e) {
            System.err.println("Error sending data to client #" + playerId + ": " + e.getMessage());
            closeConnection();
        }
    }

    public void closeConnection() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection for client #" + playerId + ": " + e.getMessage());
        } finally {
            server.removeClientHandler(this);
        }
    }

    public int getPlayerId() {
        return playerId;
    }
}