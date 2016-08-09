package com.rasbot;

import com.rasbot.camera.CameraManager;
import com.rasbot.server.ConnectionManager;
import com.rasbot.server.MessageServer;

import java.util.logging.Logger;

/**
 * Created by dawidpodolak on 23.07.2016.
 */
public class Rasbot {

    private static final Logger logger = Logger.getLogger(Rasbot.class.getName());

    /**
     * Object of network communication
     */
    private MessageServer connectionSocket;

    private GPIOManager gpioManager;

    private CameraManager cameraManager;

    public static void main(String[] args) {
        logger.info("Params: " + String.valueOf(args.length));

        new Rasbot(args);
    }

    public Rasbot(String[] params) {


        if (params.length==0 /*&& !params[0].equals("local")*/) {
            gpioManager = new GPIOManager();

            cameraManager = new CameraManager();
            cameraManager.start();
        }

        ConnectionManager connectionManager = new ConnectionManager();

        if (gpioManager != null){
            connectionManager.setMessageCallback(gpioManager.getMessageCallback());
        }
        connectionManager.init();

    }


}
