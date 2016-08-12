package com.rasbot;

import com.google.gson.Gson;
import com.rasbot.camera.CameraManager;
import com.rasbot.model.CameraSettings;
import com.rasbot.model.Message;
import com.rasbot.server.ConnectionManager;
import com.rasbot.server.MessageCallback;
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



        ConnectionManager connectionManager = new ConnectionManager();

        if (params.length==0 /*&& !params[0].equals("local")*/){
                gpioManager = new GPIOManager();

            cameraManager = new CameraManager(connectionManager);
            connectionManager.addMessageCallback(cameraManager.getMessageCallback());
            cameraManager.start();
        }

        connectionManager.addMessageCallback(new MessageCallback() {
            @Override
            public void onGetMessage(Message message) {
                CameraSettings cameraSettings = message.getCameraSettings(new Gson());
                if (cameraSettings != null){
                    logger.info(cameraSettings.toString());
                }
            }
        });


        if (gpioManager != null){
            connectionManager.addMessageCallback(gpioManager.getMessageCallback());
        }else{

        }
        connectionManager.init();

    }




}
