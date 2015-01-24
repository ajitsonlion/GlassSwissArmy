package com.communication;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;


/**
 * Created by ajit on 24.01.15.
 */


public class SocketIOSingelton {
    private static SocketIOSingelton instance = null;

    public   Socket getSocket() {

        try {
            socket=IO.socket("http://localhost");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                socket.emit("foo", "hi");
                socket.disconnect();
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {}

        });
        socket.connect();
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