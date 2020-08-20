package de.woodpecker.archery.arrowdetection.detectionserver;

import de.woodpecker.archery.arrowdetection.ArrowPosition;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Transmitter extends Thread {
    private final PrintWriter out;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean clientListeningForResults = new AtomicBoolean(false);
    private final Scanner scanner = new Scanner(System.in);
    private final ClientDispatcher dispatcher;
    private final BlockingQueue<String> sendActions;
    private final String poisonPill;
    private final DetectionController detectionController;


    public Transmitter(ClientDispatcher dispatcher, BlockingQueue<String> sendActions, String poisonPill, DetectionController detectionController) throws IOException {
        this.poisonPill = poisonPill;
        this.sendActions = sendActions;
        this.dispatcher = dispatcher;
        this.detectionController = detectionController;
        out = new PrintWriter(dispatcher.getSocket().getOutputStream(), true);
    }

    @Override
    public void run() {
        super.run();
        running.set(true);

        while (running.get()) {
            String msg = null;
            try {
                msg = sendActions.take();

                StringTokenizer tokenizer = new StringTokenizer(msg, ";");
                if (tokenizer.hasMoreTokens()) {
                    String action = tokenizer.nextToken();

                    if (action.equalsIgnoreCase("listen"))
                        startListening(tokenizer.nextToken());
                    else if (action.equalsIgnoreCase("message"))
                        sendMessage(tokenizer.nextToken());
                    else if (action.equalsIgnoreCase("arrows"))
                        startListeningToArrows();
                }
            } catch (InterruptedException e) {
                stopTransmitting();
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void startListening(String counts) throws InterruptedException {
        int i = Integer.parseInt(counts);
        for (int j = 0; j < i && running.get(); j++) {
            sendMessage("Nachricht " + j);
            sleep(100);
        }
    }

    private void startListeningToArrows() throws InterruptedException {

        while (running.get()) {
            ArrowPosition arrowPosition = detectionController.getArrowPositions().take();
            if (arrowPosition.equals(detectionController.getPoisonPill()))
                break;
            StringBuilder builder = new StringBuilder();
            builder.append(arrowPosition.getCaptured());
            builder.append(";");
            builder.append(arrowPosition.relativeX());
            builder.append(";");
            builder.append(arrowPosition.relativeY());
            sendMessage(builder.toString());
        }
    }

    public void stopTransmitting() {
        running.set(false);
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }
}
