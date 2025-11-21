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
            case "NEW_GAME":  // ADD THIS CASE
                // Reset for new game - server will create new PokerGame on next PLACE_BETS
                server.logGameEvent("Client #" + playerId + " starting new game");
                break;
            case "CONTINUE":  // ADD THIS
                processContinue();
                break;
        }
    }

    private void processContinue() {
        if (currentGame != null) {
            // Calculate and send results now that player is ready
            PokerInfo result = currentGame.calculateResult();
            result.setMessageType("GAME_RESULT");
            sendPokerInfo(result);

            String gameLog = buildGameLog(result);
            server.logGameEvent("Client #" + playerId + " - " + gameLog);

            PokerInfo roundComplete = new PokerInfo("ROUND_COMPLETE");
            roundComplete.setTotalWinnings(result.getTotalWinnings());
            roundComplete.setGameMessage(result.getGameMessage());
            sendPokerInfo(roundComplete);
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

    private String getHandTypeName(int handRank) {
        switch (handRank) {
            case ThreeCardLogic.STRAIGHT_FLUSH: return "Straight Flush";
            case ThreeCardLogic.THREE_OF_A_KIND: return "Three of a Kind";
            case ThreeCardLogic.STRAIGHT: return "Straight";
            case ThreeCardLogic.FLUSH: return "Flush";
            case ThreeCardLogic.PAIR: return "Pair";
            default: return "High Card";
        }
    }

    private void processPlay(PokerInfo clientInfo) {
        if (currentGame != null) {
            currentGame.makePlayWager();

            // Show dealer cards but DON'T calculate results yet
            PokerInfo showDealer = new PokerInfo("SHOW_DEALER");
            showDealer.setDealerHand(currentGame.getDealerHand());

            // Add dealer hand evaluation for the client to see
            int dealerHandRank = ThreeCardLogic.evalHand(currentGame.getDealerHand());
            String dealerHandType = getHandTypeName(dealerHandRank);
            boolean dealerQualifies = ThreeCardLogic.dealerQualifies(currentGame.getDealerHand());

            showDealer.setGameMessage("Dealer has: " + dealerHandType + " | Qualifies: " + (dealerQualifies ? "YES" : "NO"));
            sendPokerInfo(showDealer);

            server.logGameEvent("Client #" + playerId + " playing - dealer cards revealed");

            // STOP HERE - wait for CONTINUE message from client
            // Results will be calculated when client sends CONTINUE
        }
    }
    private String buildGameLog(PokerInfo result) {
        StringBuilder log = new StringBuilder();

        // Parse the result message to create detailed log
        String message = result.getGameMessage();
        if (message.contains("folded")) {
            log.append("folded - lost Ante and Pair Plus bets");
        } else if (message.contains("does not qualify")) {
            log.append("dealer doesn't qualify - ante pushed");
        } else if (message.contains("beat the dealer")) {
            log.append("beats dealer");
            if (message.contains("Pair Plus")) {
                log.append(" and wins Pair Plus");
            }
        } else if (message.contains("loses to dealer")) {
            log.append("loses to dealer");
            if (message.contains("Pair Plus")) {
                log.append(" but wins Pair Plus");
            } else if (message.contains("loses Pair Plus")) {
                log.append(" and loses Pair Plus");
            }
        } else if (message.contains("Push")) {
            log.append("push - tie game");
        }

        log.append(" | Total: $").append(result.getTotalWinnings());
        return log.toString();
    }

    private void processFold() {
        if (currentGame != null) {
            int totalLoss = currentGame.getAnteBet() + currentGame.getPairPlusBet();

            PokerInfo result = new PokerInfo("GAME_RESULT");
            result.setGameMessage("You folded and lost your Ante and Pair Plus bets.");
            result.setTotalWinnings(-totalLoss);
            sendPokerInfo(result);

            // Send round complete
            PokerInfo roundComplete = new PokerInfo("ROUND_COMPLETE");
            roundComplete.setTotalWinnings(-totalLoss);
            roundComplete.setGameMessage("Folded - lost bets");
            sendPokerInfo(roundComplete);

            server.logGameEvent("Client #" + playerId + " folded - lost: $" + totalLoss);
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