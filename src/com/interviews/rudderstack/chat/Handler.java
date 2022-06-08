package com.interviews.rudderstack.chat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Handler implements Runnable {

    private static final List<Handler> handlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Handler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = bufferedReader.readLine();

            handlers.add(this);
        } catch (IOException exception) {
            closeAll();
        }
    }

    @Override
    public void run() {

        while (socket.isConnected()) {
            try {
                String[] messageReceived = bufferedReader.readLine().split(":");
                String sender = messageReceived[0].trim();
                String receiver = messageReceived[1].trim();
                String messageContent = messageReceived[2].trim();

                Optional<Handler> receiverHandler =
                        handlers.stream().filter(handler -> handler.username.equals(receiver)).findFirst();
                if (receiverHandler.isPresent()) {
                    broadCastMessage(Arrays.asList(receiverHandler.get()), sender + ": " + messageContent);
                }
            } catch (IOException e) {
                closeAll();
                break;
            }
        }
    }

    public void broadCastMessage(List<Handler> handlers, String message) {

        for (Handler handler : handlers) {
            if (!this.username.equals(handler.username)) {//dont send to themselves
                handler.receive(message);
            }
        }
    }

    private void receive(String message) {
        try {
            this.bufferedWriter.write(message);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        } catch (IOException e) {
            closeAll();
        }
    }

    public void removeHandler(Handler handler) {
        handler.removeHandler(handler);
    }

    private void closeAll() {
        removeHandler(this);
        try {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


    public String getUsername() {
        return username;
    }
}
