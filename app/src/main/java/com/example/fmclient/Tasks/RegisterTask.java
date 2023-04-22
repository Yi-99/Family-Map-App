package com.example.fmclient.Tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.fmclient.DataCache;
import com.example.fmclient.ServerProxy;

import Request.RegisterRequest;
import Result.RegisterResult;

public class RegisterTask implements Runnable {
    private Handler messageHandler;
    private String host;
    private String port;
    private RegisterRequest req;
    private ServerProxy server;
    private boolean success;
    private DataCache dc;
    private String firstName;
    private String lastName;

    public RegisterTask(Handler messageHandler, RegisterRequest req, String host, String port) {
        this.messageHandler = messageHandler;
        this.req = req;
        this.host = host;
        this.port = port;
        server = new ServerProxy();
        dc = DataCache.getInstance();
    }

    @Override
    public void run() {
        RegisterResult result = server.register(host, port, req);

        if (result.getSuccess()) {
            // save data in the dataCache
            DataTask dt = new DataTask(result.getAuthtoken(), host, port);
            dt.saveData(result.getPersonID());
            firstName = dt.getFirstName();
            lastName = dt.getLastName();

            success = true;
        } else {
            success = false;
        }
        sendMessage();
    }

    private void sendMessage() {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        if (success) {//When it succeeds send the data.
            messageBundle.putString("firstname", firstName);
            messageBundle.putString("lastname", lastName);
        }
        messageBundle.putBoolean("success", success);

        message.setData(messageBundle);
        messageHandler.sendMessage(message);
    }
}
