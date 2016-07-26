package com.rasbot;

import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.SoftPwm;
import com.rasbot.util.Commands;
import com.rasbot.util.ConnectionSocket;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dawidpodolak on 23.07.2016.
 */
public class Rasbot {
    private static Logger logger = Logger.getLogger("RasBot");

    private static final int LEFT_PWM_PIN = 4;
    private static final int RIGHT_PWM_PIN = 25;

    private enum Direction{
        LEFT,RIGHT,NONE
    }

    /**
     * Indicator which rotate direction is current set
     */
    private Direction leftDirection = Direction.NONE;
    private Direction rightDirection = Direction.NONE;

    /**
     * Object of network communication
     */
    private ConnectionSocket connectionSocket;


    /**
     * Pins controls rotate direction of left and right motor
     */
    private GpioPinDigitalOutput leftDirPinA;
    private GpioPinDigitalOutput leftDirPinB;
    private GpioPinDigitalOutput rightDirPinA;
    private GpioPinDigitalOutput rightDirPinB;

    public static void main(String[] args) {
        new Rasbot();
    }

    public Rasbot() {
        com.pi4j.wiringpi.Gpio.wiringPiSetup();

        SoftPwm.softPwmCreate(LEFT_PWM_PIN, 0, 100);
        SoftPwm.softPwmCreate(RIGHT_PWM_PIN, 0, 100);

        GpioController gpio = GpioFactory.getInstance();
        leftDirPinA = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        leftDirPinA.setState(PinState.LOW);

        leftDirPinB = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05);
        leftDirPinB.setState(PinState.LOW);

        rightDirPinA = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06);
        rightDirPinA.setState(PinState.LOW);

        rightDirPinB = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_10);
        rightDirPinB.setState(PinState.LOW);

        new Thread(new Runnable() {
            public void run() {
                startRpiCamera();
            }
        }).start();

        try {
            connectionSocket = new ConnectionSocket();
            connectionSocket.initReader(onGetMessage);
        } catch (IOException ex) {
            Logger.getLogger(Rasbot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startRpiCamera(){
        try {
            String s;
            Process p = Runtime.getRuntime().exec(new String[] {"sh", "-c", Commands.START_CAMERA});
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
        } catch (IOException ex) {
            Logger.getLogger(Rasbot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    ConnectionSocket.OnGetMessage onGetMessage = new ConnectionSocket.OnGetMessage() {


        public void onGetMessage(String message) {

            try {
                JSONObject jSONObject = new org.json.JSONObject(message);

                setLeftPwm(jSONObject);

                setRightPwm(jSONObject);

            } catch (Exception ex) {
                Logger.getLogger(Rasbot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };

    private void setLeftPwm(JSONObject jSONObject) throws JSONException {
        int leftPwm = Integer.parseInt(jSONObject.getString("left_rpm"));

        if (leftDirection!=Direction.LEFT && leftPwm >5 ){
            leftDirPinA.low();
            leftDirPinB.high();
            leftDirection = Direction.LEFT;
        }else if (leftDirection != Direction.RIGHT && leftPwm < -5){
            leftDirPinA.high();
            leftDirPinB.low();
            leftDirection = Direction.RIGHT;
        }else if (leftPwm == 0){
            leftDirection = Direction.NONE;
            leftDirPinA.low();
            leftDirPinB.low();
        }

        leftPwm = Math.abs(leftPwm);
        SoftPwm.softPwmWrite(LEFT_PWM_PIN, leftPwm);
        logger.info(String.valueOf(leftPwm));
    }

    private void setRightPwm(JSONObject jSONObject) throws JSONException{
        int rightPwm = Integer.parseInt(jSONObject.getString("right_rpm"));
        logger.info("right pwm: " + String.valueOf(rightPwm));
        if (rightDirection!=Direction.LEFT && rightPwm >5 ){
            rightDirPinA.high();
            rightDirPinB.low();
            rightDirection = Direction.LEFT;
        }else if (rightDirection != Direction.RIGHT && rightPwm < -5){
            rightDirPinA.low();
            rightDirPinB.high();
            rightDirection = Direction.RIGHT;
        }else if (rightPwm == 0){
            rightDirection = Direction.NONE;
            rightDirPinA.low();
            rightDirPinB.low();
        }

        rightPwm = Math.abs(rightPwm);
        SoftPwm.softPwmWrite(RIGHT_PWM_PIN, rightPwm);
    }


}
