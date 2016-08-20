/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rasbot.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rasbot.model.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dawid
 */
public class MessageServer {


    private static final Logger logger = Logger.getLogger(MessageServer.class.getName());
    private ServerSocket serverSocket;
    private Socket socket;

    private PrintWriter printWriter;

    private int port = 4333;
    private SocketClientHandler socketClientHandler;

    private MessageCallback messageCallback;

    public MessageServer() {
        try {
            serverSocket = new ServerSocket(port, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(() -> {
            while (true)
            {
                try {
                    logger.info("MessageServer:start - before");
                    socket = serverSocket.accept();
                    logger.info("MessageServer:start - after");

                    printWriter = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())),
                            true);

                    socketClientHandler = new SocketClientHandler(socket);

                    socketClientHandler.setCallback(messageCallback);
                    Thread thread = new Thread(socketClientHandler);
                    thread.start();
                    logger.info("MessageServer:start - done");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void setMessageCallback(MessageCallback messageCallback){
        this.messageCallback = messageCallback;
    }

    public void sendMessage(Message message) {
        logger.info("send: " + message.getJsonMessage());
        printWriter.println(message.getJsonMessage());
        printWriter.flush();
    }


    public class SocketClientHandler implements Runnable {

        private Socket client;
        private MessageCallback callback;

        public SocketClientHandler(Socket client) {
            this.client = client;
        }

        public void setCallback(MessageCallback callback) {
            this.callback = callback;
        }

        public void run() {
            try {
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String userInput;
                Gson gson = new GsonBuilder().create();
                while ((userInput = stdIn.readLine()) != null) {
                    logger.info("received: " + userInput);
                    Message m = gson.fromJson(userInput, Message.class);
                    if (callback != null && m != null){
                        callback.onGetMessage(m);
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(MessageServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}
