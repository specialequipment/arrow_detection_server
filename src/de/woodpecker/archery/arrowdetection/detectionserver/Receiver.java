package de.woodpecker.archery.arrowdetection.detectionserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Receiver extends Thread {
    private final BufferedReader in;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ClientDispatcher dispatcher;
    private final BlockingQueue<String> messages;
    private final String poisonPill;

    public Receiver(ClientDispatcher dispatcher, BlockingQueue<String> messages, String poisonPill) throws IOException {
        this.messages = messages;
        this.poisonPill = poisonPill;
        this.dispatcher = dispatcher;
        in = new BufferedReader(new InputStreamReader(dispatcher.getSocket().getInputStream()));
    }

    @Override
    public void run() {
        super.run();
        running.set(true);

        while (running.get()) {
            String msg = awaitMessage();
            System.out.println("Received '" + msg + "' from Client " + dispatcher.getSocket().toString());
            if (msg != null && !msg.isEmpty())
                messages.add(msg);
        }
    }

    public void stopReceiving() {
        running.set(false);
    }

    public String awaitMessage() {
        try {
            return in.readLine();
        } catch (IOException e) {
            stopReceiving();
            return e.getMessage();
        }
    }
}
