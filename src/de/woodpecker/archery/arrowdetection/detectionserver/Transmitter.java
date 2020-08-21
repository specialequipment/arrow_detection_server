package de.woodpecker.archery.arrowdetection.detectionserver;

import de.woodpecker.archery.arrowdetection.ArrowPosition;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Transmitter extends Thread {
    private final PrintWriter out;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final BlockingQueue<String> sendActions;
    private final DetectionController detectionController;


    public Transmitter(ClientDispatcher dispatcher, BlockingQueue<String> sendActions, String poisonPill, DetectionController detectionController) throws IOException {
        this.sendActions = sendActions;
        this.detectionController = detectionController;
        out = new PrintWriter(dispatcher.getSocket().getOutputStream(), true);
    }

    @Override
    public void run() {
        super.run();
        running.set(true);

        while (running.get()) {
            String msg;
            try {
                msg = sendActions.take();

                StringTokenizer tokenizer = new StringTokenizer(msg, ";");
                if (tokenizer.hasMoreTokens()) {
                    String action = tokenizer.nextToken();

                    if (action.equalsIgnoreCase("message"))
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

    private void startListeningToArrows() throws InterruptedException {

        while (running.get()) {
            ArrowPosition arrowPosition = detectionController.getArrowPositions().take();
            if (arrowPosition.equals(detectionController.getPoisonPill()))
                break;
            String message = arrowPosition.getCaptured() +
                    ";" +
                    arrowPosition.relativeX() +
                    ";" +
                    arrowPosition.relativeY();
            sendMessage(message);
        }
    }

    public void stopTransmitting() {
        running.set(false);
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }
}
