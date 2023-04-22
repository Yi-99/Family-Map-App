package com.example.fmclient;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Request.LoginRequest;
import Request.RegisterRequest;
import Result.EventsResult;
import Result.LoginResult;
import Result.PersonsResult;
import Result.RegisterResult;

public class ServerProxyTest {
    private static ServerProxy server;
    private static LoginRequest logReqSuccess;
    private static LoginRequest logReqFail;
    private static LoginResult logResSuccess;
    private static LoginResult logResFail;
    private static RegisterRequest registerReqSuccess;
    private static RegisterRequest registerReqFail;
    private static RegisterResult registerResSuccess;
    private static RegisterResult registerResFail;
    private static String host = "localhost";
    private static String port = "8080";
    private static String error = "Error";
    private String authtoken = "cc0c6c3b-dfef-4937-95f3-b5ccdf84f9da";

    @Before
    public void setUp() {
        server = ServerProxy.initialize();
        server.setHost(host);
        server.setPort(port);

        String authToken = "1";
        String username = "joe";
        String password = "lim";
        String personID = "yirang_lim";
        String email = "yirang@gmail.com";
        String firstName = "yirang";
        String lastName = "lim";
        String gender = "m";

        // Register Test Set Up
        registerReqSuccess = new RegisterRequest(username, password, email, firstName, lastName, gender);
        registerReqFail = new RegisterRequest(null, null, null, null, null, null);
        registerResSuccess = new RegisterResult(authToken, username, personID, true);
        registerResFail = new RegisterResult(error, false);

        // Login Test Set Up
        logReqSuccess = new LoginRequest(username, password);
        logReqFail = new LoginRequest(null, null);
        logResSuccess = new LoginResult("Success", true);
        logResFail = new LoginResult(error, false);
    }

    @Test
    public void registerSuccess() {
        RegisterResult res = server.register(host, port, registerReqSuccess);
        authtoken = res.getAuthtoken();
        Assert.assertNotNull(res);
        Assert.assertNotNull(res.getAuthtoken());
        Assert.assertNotNull(res.getPersonID());
    }

    @Test
    public void registerFail() {
        RegisterResult resFail = server.register(host, port, registerReqFail);
        Assert.assertNotNull(resFail);
        Assert.assertNull(resFail.getPersonID());
        Assert.assertNull(resFail.getAuthtoken());
        Assert.assertNull(resFail.getUsername());
        Assert.assertEquals(registerResFail.getSuccess(), resFail.getSuccess());
    }

    @Test
    public void LoginSuccess() {
        LoginResult res = server.login(host, port, logReqSuccess);
        Assert.assertNull(res.getMessage());
        Assert.assertNotNull(res.getAuthtoken());
        Assert.assertEquals(logResSuccess.getSuccess(), res.getSuccess());
    }

    @Test
    public void loginFail() {
        LoginResult res = server.login(host, port, logReqFail);
        Assert.assertNull(res.getAuthtoken());
        Assert.assertNull(res.getUsername());
        Assert.assertNull(res.getPersonID());
        Assert.assertEquals(logResFail.getSuccess(), res.getSuccess());
    }

    @Test
    public void getPeopleSuccess() {
        PersonsResult res = server.getPeople(host, port, authtoken);
        Assert.assertNotNull(res);
        Assert.assertNotNull(res.getData());
        Assert.assertNull(res.getMessage());
        Assert.assertTrue(res.getSuccess());
    }

    @Test
    public void getPeopleFail() {
        PersonsResult res = server.getPeople(host, port, " ");
        Assert.assertNotNull(res);
        Assert.assertNull(res.getData());
        Assert.assertNotNull(res.getMessage());
        Assert.assertFalse(res.getSuccess());
    }

    @Test
    public void getEventsSuccess(){
        EventsResult res = server.getEvents(host, port, authtoken);
        Assert.assertNotNull(res);
        Assert.assertNotNull(res.getData());
        Assert.assertNull(res.getMessage());
        Assert.assertTrue(res.getSuccess());
    }

    @Test
    public void getEventsFail(){
        EventsResult res = server.getEvents(host ,port, " ");
        Assert.assertNotNull(res);
        Assert.assertNull(res.getData());
        Assert.assertNotNull(res.getMessage());
        Assert.assertFalse(res.getSuccess());
    }
}
