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
public class CameraManager implements MessageCallback{

    private Gson gson = new Gson();
    private static final Logger logger = Logger.getLogger(CameraManager.class.getName());
    private ConnectionManager connectionManager;

    public CameraManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    private MessageCallback messageCallback = message -> {
        CameraSettings cameraSettings = message.getCameraSettings(gson);

        if (cameraSettings == null){
            return;
        }

        logger.info(cameraSettings.toString());

        CameraCommand.CameraCommandBuilder builder = new CameraCommand.CameraCommandBuilder();
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


        logger.info("command: " + builder.build().get());
        runCommadCamera(builder.build());
        connectionManager.sendMessage(message);
    };



    public void start() {
        new Thread(() -> {
            runCommadCamera(new CameraCommand.CameraCommandBuilder().build());
        }).start();
    }

    private void runCommadCamera(CameraCommand cameraCommand) {
        terminatePrevRanCamera();

        Logger.getLogger("CameraManager").info(cameraCommand.get());

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
    public void onGetMessage(Message controlMessage) {

    }
}
