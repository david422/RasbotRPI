package com.rasbot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by dawidpodolak on 05.08.2016.
 */
public class Utils {
    public static void terminatePreviousRanServer(int port){
        String command = String.format("kill `lsof -i -n -P | grep TCP | grep %d | tr -s \" \" \"\\n\" | sed -n 2p`", port);
        try {
            Runtime.getRuntime().exec(new String[] {"sh", "-c", command});

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String runCommand(String command) {

        StringBuilder response = new StringBuilder();
        String s;
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));


            while ((s = br.readLine()) != null) {
                response.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }
}
