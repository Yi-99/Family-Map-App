package com.example.fmclient.Tasks;

import com.example.fmclient.DataCache;
import com.example.fmclient.ServerProxy;

import Result.EventsResult;
import Result.PersonsResult;

public class DataTask {
    private final String authToken;
    private final ServerProxy server;
    private final DataCache dc;
    private String host;
    private String port;
    private String firstName;
    private String lastName;

    public DataTask(String authToken, String serverHost, String serverPort){
        this.authToken = authToken;
        server = new ServerProxy();
        host = serverHost;
        port = serverPort;
        server.setHost(serverHost);
        server.setPort(serverPort);
        dc = DataCache.getInstance();
    }

    public void saveData(String personID){
        PersonsResult personsResult = server.getPeople(host, port, authToken);
        EventsResult eventsResult = server.getEvents(host, port, authToken);
        dc.saveData(personID, personsResult, eventsResult);
        firstName = dc.getUserPerson().getFirstName();
        lastName = dc.getUserPerson().getLastName();
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }
}
