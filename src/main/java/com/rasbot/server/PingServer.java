package com.rasbot.server;

import com.rasbot.util.Utils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Created by dawidpodolak on 05.08.2016.
 */
public class PingServer implements PingTimeCallback {

    private Logger logger = Logger.getLogger("PingServer");

    public static final int PORT = 4334;

    private static final int CONNECTION_TRESHOLD = 750;

    private ServerSocket serverSocket;

    private Socket socket;

    private boolean connectionEstablished = false;
    private boolean prevConnectionState = false;

    private ConnectionCallback connectionCallback;

    private long pingTime;



    public PingServer() {
        try {
            Utils.terminatePreviousRanServer(PORT);

            serverSocket = new ServerSocket(PORT, 1);
            logger.info("Ping server initialized!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void start(){
        new Thread(() -> {
            while (true){

                try {
                    socket = serverSocket.accept();

                    Thread pingThread = new Thread(new PingSocketHandler(this, socket));
                    pingThread.start();

                    startTimer();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void startTimer() {
        Timer pingTimer = new Timer();


        pingTimer.schedule(new TimerTask() {

            long prevPingTime;

            @Override
            public void run() {
                connectionEstablished = (prevPingTime - pingTime) < CONNECTION_TRESHOLD;

                if (connectionCallback != null){
                    if (connectionEstablished && connectionEstablished != prevConnectionState){
                        connectionCallback.onConnectionEstablished();
                        prevConnectionState = connectionEstablished;
                    }else if (!connectionEstablished && connectionEstablished != prevConnectionState){
                        connectionCallback.onConnectionInterrupted();
                        prevConnectionState = connectionEstablished;
                    }
                }

                prevPingTime = System.currentTimeMillis();
            }
        }, 0, 100);
    }

    public boolean isConnection(){
        return connectionEstablished;
    }

    public void setConnectionCallback(ConnectionCallback connectionCallback) {
        this.connectionCallback = connectionCallback;
    }

    @Override
    public void onPing(long pingTime) {
        this.pingTime = pingTime;
    }

    public static class PingSocketHandler implements Runnable {

        private Socket socket;

        private PingTimeCallback pingTimeCallback;

        public PingSocketHandler(PingTimeCallback pingTimeCallback, Socket socket) {
            this.pingTimeCallback = pingTimeCallback;
            this.socket = socket;
        }

        @Override
        public void run() {

            PrintWriter pingPW = null;
            BufferedReader pingBR = null;
            try {
                pingPW = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

                pingBR = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String pingMessage;

                while ((pingMessage = pingBR.readLine()) != null){

                    pingTimeCallback.onPing(System.currentTimeMillis());

                    pingPW.write(pingMessage + "\n");
                    pingPW.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (pingBR != null) {
                    try {
                        pingBR.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (pingPW != null) {
                    pingPW.close();
                }
            }
        }
    }


}
