package com.interviews.rudderstack.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    private final ServerSocket serverSocket;

    public ChatServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(1234);
        ChatServer chatServer = new ChatServer(serverSocket);
        chatServer.start();
    }

    private void start() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                Handler clientHandler = new Handler(socket);
                new Thread(clientHandler).start();

                System.out.println("A new client " + clientHandler.getUsername() + " got connected to the server");
            }
        } catch (IOException e) {
            closeAll();
            e.printStackTrace();
        }
    }

    private void closeAll() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
