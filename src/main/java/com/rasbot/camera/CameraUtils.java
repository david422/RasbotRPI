package com.rasbot.camera;

import com.google.gson.Gson;
import com.rasbot.model.CameraSettings;

import java.io.*;
import java.util.logging.Logger;

/**
 * Created by dpodolak on 19.08.16.
 */
public class CameraUtils {

    private static final Logger logger = Logger.getLogger(CameraUtils.class.getName());

    private static final String SETTINGS_FILE = "camera.settings";

    public static void saveSettings(CameraSettings settings, Gson gson){
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

    public static CameraSettings readCameraSettings(Gson gson){
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
