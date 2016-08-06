package com.rasbot.camera;

import com.rasbot.util.Utils;

import java.util.logging.Logger;

/**
 * Created by dawidpodolak on 05.08.2016.
 */
public class CameraManager {

    private static final Logger logger = Logger.getLogger(CameraManager.class.getName());

    public void start() {
        new Thread(() -> {
            runCommadCamera();
        }).start();
    }

    private void runCommadCamera() {
        terminatePrevRanCamera();

            CameraCommand cameraCommand = new CameraCommand.CameraCommandBuilder().build();

        Utils.runCommand(cameraCommand.get());
    }

    private void terminatePrevRanCamera() {
        String pidResponse = Utils.runCommand("ps -ef | grep raspivid | tr -s \" \" \"\\n\" | sed -n 2p");
        Utils.runCommand(String.format("kill -9 %s", pidResponse));
    }


}
