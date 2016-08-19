package com.rasbot.camera;

import com.google.gson.Gson;
import com.rasbot.model.CameraSettings;
import com.rasbot.model.Message;
import com.rasbot.server.ConnectionManager;
import com.rasbot.server.MessageCallback;
import com.rasbot.util.Utils;

import java.util.logging.Logger;

/**
 * Created by dawidpodolak on 05.08.2016.
 */
public class RaspberryCameraManager implements MessageCallback, CameraManager{

    private Gson gson = new Gson();
    private static final Logger logger = Logger.getLogger(RaspberryCameraManager.class.getName());
    private ConnectionManager connectionManager;

    public RaspberryCameraManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }


    private MessageCallback messageCallback = message -> {

        logger.info("camera received settings");
        if (message == null){
            return;
        }

        if (message.getMessageType() == null){
            return;
        }

        switch (message.getMessageType()){
            case Message.CAMERA:
              setCameraSettings(message);
                break;
            case Message.READ_SETTINGS:
                CameraSettings settings = CameraUtils.readCameraSettings(gson);
                Message m = new Message(gson.toJson(settings));
                connectionManager.sendMessage(m);
                break;
        }
    };

    private void setCameraSettings(Message message) {
        CameraSettings cameraSettings = message.getCameraSettings(gson);

        if (cameraSettings == null){
            return;
        }

        logger.info(cameraSettings.toString());

        CameraCommand.Builder builder = getCommandBuilder(cameraSettings);

        CameraUtils.saveSettings(cameraSettings, gson);

        logger.info("command: " + builder.build().get());
        runCommadCamera(builder.build());
        connectionManager.sendMessage(message);
    }


    public void start() {
        new Thread(() -> {
            CameraSettings cameraSettings = CameraUtils.readCameraSettings(gson);

            CameraCommand.Builder builder;
            if (cameraSettings != null){
                builder = getCommandBuilder(cameraSettings);
            }else{
                builder = new CameraCommand.Builder();
            }

            runCommadCamera(builder.build());
        }).start();
    }

    private CameraCommand.Builder getCommandBuilder(CameraSettings cameraSettings){
        CameraCommand.Builder builder = new CameraCommand.Builder();
        if (cameraSettings.getResolution() != null){
            String[] widthHeiht = cameraSettings.getResolution().split("x");
            builder.setWidthRes(Integer.parseInt(widthHeiht[0]));
            builder.setHeighRes(Integer.parseInt(widthHeiht[1]));
        }

        if (cameraSettings.getBrightness() != null){
            builder.setBrightness((int) Float.parseFloat(cameraSettings.getBrightness()));
        }

        if (cameraSettings.getFps() != null){
            builder.setFps((int) Float.parseFloat(cameraSettings.getFps()));
        }

        if (cameraSettings.getFlipHorizontal() != null){
            builder.setHorizontalFlip(Boolean.parseBoolean(cameraSettings.getFlipHorizontal()));
        }

        if (cameraSettings.getFlipVertical() != null){
            builder.setVerticalFlip(Boolean.parseBoolean(cameraSettings.getFlipVertical()));
        }

        return builder;
    }

    private void runCommadCamera(CameraCommand cameraCommand) {
        terminatePrevRanCamera();

        Logger.getLogger("RaspberryCameraManager").info(cameraCommand.get());

        Utils.runCommand(cameraCommand.get());
    }

    private void terminatePrevRanCamera() {
        String pidResponse = Utils.runCommand("ps -ef | grep raspivid | tr -s \" \" \"\\n\" | sed -n 2p");
        Utils.runCommand(String.format("kill -9 %s", pidResponse));
    }


    public MessageCallback getMessageCallback() {
        return messageCallback;
    }


    @Override
    public void onGetMessage(Message message) {
        logger.info("camera received settings");
        if (message == null){
            return;
        }

        if (message.getMessageType() == null){
            return;
        }

        switch (message.getMessageType()){
            case Message.CAMERA:
                setCameraSettings(message);
                break;
            case Message.READ_SETTINGS:
                CameraSettings settings = CameraUtils.readCameraSettings(gson);
                Message m = new Message(gson.toJson(settings));
                connectionManager.sendMessage(m);
                break;
        }
    }
}
