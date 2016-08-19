package com.rasbot;

import com.rasbot.camera.CameraManager;
import com.rasbot.camera.LocalCameraManager;
import com.rasbot.camera.RaspberryCameraManager;
import com.rasbot.gpio.GPIOManager;
import com.rasbot.gpio.LocalGPIOEmulator;
import com.rasbot.gpio.RaspberryGPIOManager;
import com.rasbot.server.ConnectionManager;

import java.util.logging.Logger;

/**
 * Created by dawidpodolak on 23.07.2016.
 */
public class Rasbot {

    private static final Logger logger = Logger.getLogger(Rasbot.class.getName());


    private GPIOManager gpioManager;

    private CameraManager cameraManager;

    public static void main(String[] args) {
        logger.info("Params: " + String.valueOf(args.length));

        new Rasbot(args);
    }

    public Rasbot(String[] params) {

        ConnectionManager connectionManager = new ConnectionManager();

        if (params.length==0 /*&& !params[0].equals("local")*/){
            gpioManager = new RaspberryGPIOManager();
            cameraManager = new RaspberryCameraManager(connectionManager);
        }else{
            gpioManager = new LocalGPIOEmulator();
            cameraManager = new LocalCameraManager(connectionManager);
        }

        connectionManager.addMessageCallback(cameraManager.getMessageCallback());
        cameraManager.start();
        connectionManager.addMessageCallback(gpioManager.getMessageCallback());

        connectionManager.init();

    }
}
