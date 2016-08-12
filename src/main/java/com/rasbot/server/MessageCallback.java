package com.rasbot.server;

import com.rasbot.model.Message;

public interface MessageCallback {

        public void onGetMessage(Message controlMessage);
}