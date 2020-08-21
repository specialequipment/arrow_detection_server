package de.woodpecker.archery.arrowdetection.detectionserver;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientDispatcher extends Thread {
    private final BlockingQueue<String> messages;
    private final BlockingQueue<String> sendActions;

    private final Socket socket;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Receiver receiver;
    private final Transmitter transmitter;

    public ClientDispatcher(Socket socket, Server server, DetectionController detectionController) throws IOException {
        this.socket = socket;
        messages = new LinkedBlockingDeque<>();
        sendActions = new LinkedBlockingDeque<>();
        String poisonPill = "#";
        this.receiver = new Receiver(this, messages, poisonPill);
        this.transmitter = new Transmitter(this, sendActions, poisonPill, detectionController);
    }

    private void processMessage(String msg) throws InterruptedException {
        System.out.println(msg);

        if (msg.equalsIgnoreCase("exit")) {
            running.set(false);
            stopClientDispatcher();
        } else {
            sendActions.put(msg);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void stopClientDispatcher() {
        running.set(false);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        running.set(true);
        receiver.start();
        transmitter.start();

        while (running.get()) {
            try {
                String msg = messages.take();
                processMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
