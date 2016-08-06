package com.rasbot.server;

import com.rasbot.model.Control;

public interface MessageCallback {

        public void onGetMessage(Control controlMessage);
}