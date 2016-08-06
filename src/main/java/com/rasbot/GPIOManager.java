package com.rasbot;

import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.SoftPwm;
import com.rasbot.model.Control;
import com.rasbot.server.MessageCallback;

import java.util.logging.Logger;

/**
 * Created by dawidpodolak on 05.08.2016.
 */
public class GPIOManager {

    private static Logger logger = Logger.getLogger("RasBot");

    private enum Direction{
        LEFT,RIGHT,NONE
    }


    private static final int LEFT_PWM_PIN = 4;
    private static final int RIGHT_PWM_PIN = 25;

    /**
     *
     * This is a test of input lag
     * Indicator which rotate direction is current set
     */
    private Direction leftDirection = Direction.NONE;
    private Direction rightDirection = Direction.NONE;

    /**
     * Pins controls rotate direction of left and right motor
     */
    private GpioPinDigitalOutput leftDirPinA;
    private GpioPinDigitalOutput leftDirPinB;
    private GpioPinDigitalOutput rightDirPinA;
    private GpioPinDigitalOutput rightDirPinB;


    public GPIOManager() {
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
    }

    private void setLeftPwm(Control control){
        int leftPwm = control.getLeftPwm();

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

    private void setRightPwm(Control control){
        int rightPwm = control.getRightPwm();
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

    private MessageCallback onGetMessage = message -> {

        logger.info(message.toString());
            setLeftPwm(message);

            setRightPwm(message);
    };

    public MessageCallback getMessageCallback(){
        return onGetMessage;
    }
}
