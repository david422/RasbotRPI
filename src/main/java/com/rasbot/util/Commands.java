/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rasbot.util;

/**
 *
 * @author dawid
 */
public class Commands {
//    public static final String START_CAMERA = "raspivid -t 999999 -w 720 -h 480 -fps 20 -b 2000000 -o - | \\gst-launch-1.0 -v fdsrc ! h264parse ! rtph264pay config-interval=1 pt=96 \\! gdppay ! tcpserversink host=192.168.2.1 port=5000";
    public static final String START_CAMERA = "nohup raspivid -rot 180 -t 0 -h 360 -w 640 -fps 25 -hf -vf -b 2000000 -o - | gst-launch-1.0 -v fdsrc ! h264parse ! rtph264pay config-interval=1 pt=96 ! gdppay ! tcpserversink host=192.168.2.1 port=8554 ";

    public static final String mk = "mkdir /home/pi/test";
}
