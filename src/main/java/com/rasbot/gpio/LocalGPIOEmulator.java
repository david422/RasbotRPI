package com.rasbot.gpio;

import com.rasbot.model.Message;
import com.rasbot.server.MessageCallback;

import java.util.logging.Logger;

/**
 * Created by dpodolak on 19.08.16.
 */
public class LocalGPIOEmulator implements GPIOManager {

    private static final Logger logger = Logger.getLogger(LocalGPIOEmulator.class.getName());

    @Override
    public MessageCallback getMessageCallback() {
        return controlMessage -> {
            logger.info("get message: " +controlMessage);
        };
    }
}
