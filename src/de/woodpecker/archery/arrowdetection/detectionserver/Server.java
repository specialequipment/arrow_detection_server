package de.woodpecker.archery.arrowdetection.detectionserver;

import de.woodpecker.archery.arrowdetection.AnalyserSettings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server extends Thread {
    private final List<ClientDispatcher> clientDispatchers = new ArrayList();
    private final DetectionController detectionController;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ServerSocket serverSocket;

    public Server(AnalyserSettings settings) {
        detectionController = new DetectionController(settings);
    }

    @Override
    public void run() {
        running.set(true);
        startServer(5555);
    }

    private void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            detectionController.start();

            while (running.get()) {
                Socket socket = serverSocket.accept();
                ClientDispatcher dispatcher = new ClientDispatcher(socket, this, detectionController);
                clientDispatchers.add(dispatcher);
                dispatcher.start();
                System.out.println("Neuer Client verbunden, Clients aktuell: " + clientDispatchers.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        running.set(false);
        // Serversocket schließen
        for (ClientDispatcher clientDispatcher : clientDispatchers) {
            clientDispatcher.stopClientDispatcher();
        }
    }
}
