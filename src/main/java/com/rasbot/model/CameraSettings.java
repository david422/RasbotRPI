package com.rasbot.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dawidpodolak on 08.08.2016.
 */
public class CameraSettings {

    @SerializedName("res")
    private String resolution;

    @SerializedName("fps")
    private String fps;

    @SerializedName("brgnss")
    private String brightness;

    @SerializedName("fv")
    private String flipVertical;

    @SerializedName("fh")
    private String flipHorizontal;

    @Override
    public String toString() {
        return String.format("res: %s, fps: %s, brightness: %s, fv: %s, fh: %s", resolution, fps, brightness, flipVertical, flipHorizontal);
    }

    public String getResolution() {
        return resolution;
    }

    public String getFps() {
        return fps;
    }

    public String getBrightness() {
        return brightness;
    }

    public String getFlipVertical() {
        return flipVertical;
    }

    public String getFlipHorizontal() {
        return flipHorizontal;
    }
}
