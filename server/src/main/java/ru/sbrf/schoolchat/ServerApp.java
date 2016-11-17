package ru.sbrf.schoolchat;

import ru.sbrf.schoolchat.server.ChatServer;

import java.util.concurrent.TimeUnit;

/**
 * Created by Ivan on 17/11/2016.
 */
public class ServerApp {
    public static void main(String[] args) {
        ChatServer server = new ChatServer(TimeUnit.SECONDS.toMillis(30));
        server.run();
    }
}
