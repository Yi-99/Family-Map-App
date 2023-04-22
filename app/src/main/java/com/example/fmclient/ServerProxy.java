package com.example.fmclient;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import Request.LoginRequest;
import Request.RegisterRequest;
import Result.ClearResult;
import Result.EventsResult;
import Result.LoginResult;
import Result.PersonsResult;
import Result.RegisterResult;

public class ServerProxy {
    private static ServerProxy server;
    private String host;
    private String port;
    Gson gson = new Gson();

    public void setHost(String host) { this.host = host; }
    public void setPort(String port) { this.port = port; }

    public synchronized static ServerProxy initialize() {
        if (server == null) server = new ServerProxy();
        return server;
    }

    public LoginResult login(String host, String port, LoginRequest r) {
        ServerProxy.initialize();
        try {
            URL url = new URL("http://" + host + ":" + port + "/user/login");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "applciation/json");
            http.connect();

            String req = gson.toJson(r);
            OutputStream body = http.getOutputStream();
            writeString(req, body);

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resBody = http.getInputStream();
                String res = readString(resBody);
                return gson.fromJson(res, LoginResult.class);
            } else return new LoginResult(http.getResponseMessage(), false);

        } catch (IOException e) {
            e.printStackTrace();
            return new LoginResult("Error with login", false);
        }
    }

    public RegisterResult register(String host, String port, RegisterRequest req) {
        ServerProxy.initialize();
        try {
            URL url = new URL("http://" + host + ":" + port + "/user/register");
            HttpURLConnection http;
            http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            String reqInfo = gson.toJson(req);
            OutputStream body = http.getOutputStream();
            writeString(reqInfo, body);
            body.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resBody = http.getInputStream();
                String resData = readString(resBody);
                RegisterResult res = gson.fromJson(resData, RegisterResult.class);
                return res;
            } else {
                RegisterResult res = new RegisterResult(http.getResponseMessage(), false);
                return res;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new RegisterResult("Error with Registering User", false);
        }
    }

    public PersonsResult getPeople(String host, String port, String authtoken) {
        try {
            URL url = new URL("http://" + host + ":" + port + "/person");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Accept", "application/json");
            http.addRequestProperty("Authorization", authtoken);
            http.connect();

            PersonsResult res;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resp = http.getInputStream();
                String respData = readString(resp);
                res = gson.fromJson(respData, PersonsResult.class);
            } else {
                InputStream resp = http.getErrorStream();
                String respData = readString(resp);
                res = gson.fromJson(respData, PersonsResult.class);
            }

            return res;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public EventsResult getEvents(String host, String port, String authtoken) {
        try {
            // attempt to make an HTTP request
            URL url = new URL("http://" + host + ":" + port + "/event");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Accept", "application/json");
            http.addRequestProperty("Authorization", authtoken);
            http.connect();

            // store the data successfully extracted from an HTTP request
            EventsResult res;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resp = http.getInputStream();
                String respData = readString(resp);
                res = gson.fromJson(respData, EventsResult.class);
            } else {
                InputStream resp = http.getErrorStream();
                String respData = readString(resp);
                res = gson.fromJson(respData, EventsResult.class);
            }

            return res;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public ClearResult clear(String host, String port, String authtoken) {
        try {
            // attempt to make an HTTP request
            URL url = new URL("http://" + host + ":" + port + "/clear");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("DELETE");
            http.setDoOutput(false);
            http.addRequestProperty("Accept", "application/json");
            http.addRequestProperty("Authorization", authtoken);
            http.connect();

            // store the data successfully extracted from an HTTP request
            ClearResult res;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resp = http.getInputStream();
                String respData = readString(resp);
                res = gson.fromJson(respData, ClearResult.class);
            } else {
                InputStream resp = http.getErrorStream();
                String respData = readString(resp);
                res = gson.fromJson(respData, ClearResult.class);
            }

            return res;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String readString(InputStream i) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(i);
        char[] buf = new char[1024];
        int length;
        while ((length = sr.read(buf)) > 0) {
            sb.append(buf, 0 , length);
        }
        return sb.toString();
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(os);
        osw.write(str);
        osw.flush();
    }
}
