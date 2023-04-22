package com.example.fmclient.Tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.fmclient.DataCache;
import com.example.fmclient.ServerProxy;

import Request.LoginRequest;
import Result.EventsResult;
import Result.LoginResult;
import Result.PersonsResult;

public class LoginTask implements Runnable {
    private Handler messageHandler;
    private String host;
    private String port;
    private LoginRequest req;
    private ServerProxy server;
    private boolean success;
    private DataCache dc;
    private String firstName;
    private String lastName;

    public LoginTask(Handler messageHandler, LoginRequest req, String host, String port) {
        server = new ServerProxy();
        this.messageHandler = messageHandler;
        this.req = req;
        this.host = host;
        this.port = port;
        dc = DataCache.getInstance();
    }

    @Override
    public void run() {
        LoginResult result = server.login(host, port, req);

        if (result.getSuccess()) {
            DataTask dt = new DataTask(result.getAuthtoken(), host, port);
            dt.saveData(result.getPersonID());
            PersonsResult personsResult = server.getPeople(host, port, result.getAuthtoken());
            EventsResult eventsResult = server.getEvents(host, port, result.getAuthtoken());
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
