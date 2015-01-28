package com.communication;


import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.transport.SocketIOTransport;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;


/**
 * Created by ajit on 24.01.15.
 */


public class SocketIOSingelton {
    private static SocketIOSingelton instance = null;

    public   Socket getSocket() {

        try {
            socket = IO.socket("http://thesis-ajitsonlion.c9.io/");
            socket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);

        }
        return socket;
    }

    public   void setSocket(Socket socket) {
        this.socket = socket;
    }

    private Socket socket;
    protected SocketIOSingelton() {
        // Exists only to defeat instantiation.
    }
    public static SocketIOSingelton getInstance() throws URISyntaxException {
        if(instance == null) {
            instance = new SocketIOSingelton();

        }
        return instance;
    }


}