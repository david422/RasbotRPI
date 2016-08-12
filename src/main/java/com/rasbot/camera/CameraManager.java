package com.rasbot.camera;

import com.google.gson.Gson;
import com.rasbot.model.CameraSettings;
import com.rasbot.model.Message;
import com.rasbot.server.ConnectionManager;
import com.rasbot.server.MessageCallback;
import com.rasbot.util.Utils;

import java.io.*;
import java.util.logging.Logger;

/**
 * Created by dawidpodolak on 05.08.2016.
 */
public class CameraManager implements MessageCallback{

    private Gson gson = new Gson();
    private static final Logger logger = Logger.getLogger(CameraManager.class.getName());
    private ConnectionManager connectionManager;
    private String SETTINGS_FILE = "camera.settings";

    public CameraManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    private MessageCallback messageCallback = message -> {

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
            case Message.SETTINGS:
                CameraSettings settings = readCameraSettings();
                Message m = new Message(gson.toJson(settings));
                connectionManager.sendMessage(m);
                break;
        }

        setCameraSettings(message);
    };

    private void setCameraSettings(Message message) {
        CameraSettings cameraSettings = message.getCameraSettings(gson);

        if (cameraSettings == null){
            return;
        }

        logger.info(cameraSettings.toString());

        CameraCommand.Builder builder = getCommandBuilder(cameraSettings);

        saveSettings(cameraSettings);

        logger.info("command: " + builder.build().get());
        runCommadCamera(builder.build());
        connectionManager.sendMessage(message);
    }


    public void start() {
        new Thread(() -> {
            CameraSettings cameraSettings = readCameraSettings();

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

    public void saveSettings(CameraSettings settings){
        File settingsFile = new File(SETTINGS_FILE);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(settingsFile);
            String textToSave = gson.toJson(settings);
            logger.info("save json settings: " + textToSave);
            fos.write(textToSave.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public CameraSettings readCameraSettings(){
        File settings = new File(SETTINGS_FILE);
        CameraSettings cameraSettings = null;

        if (!settings.exists()){
            return null;
        }
            FileReader fr = null;
            BufferedReader br = null;
        try {
            fr = new FileReader(settings);
            br = new BufferedReader(fr);

            String json = br.readLine();
            logger.info("read json settings: " + json);
            cameraSettings = gson.fromJson(json, CameraSettings.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return cameraSettings;
    }
}
