package com.rasbot.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dawidpodolak on 06.08.2016.
 */
public class Control {

    @SerializedName("left_rpm")
    private int leftPwm;

    @SerializedName("right_rpm")
    private int rightPwm;

    public Control(int leftPwm, int rightPwm) {
        this.leftPwm = leftPwm;
        this.rightPwm = rightPwm;
    }

    public int getLeftPwm() {
        return leftPwm;
    }

    public int getRightPwm() {
        return rightPwm;
    }

    @Override
    public String toString() {
        return String.format("Control message - LeftPWM: %d, RightPWM: %d", leftPwm, rightPwm);
    }

    public static String buildZeroControl() {
        return String.format("{\"left_pwm\":0,\"right_pwm\":0}");
    }
}
