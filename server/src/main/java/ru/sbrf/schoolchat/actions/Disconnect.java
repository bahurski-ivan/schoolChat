package ru.sbrf.schoolchat.actions;

import ru.sbrf.schoolchat.objects.TextMessage;
import ru.sbrf.schoolchat.remoteclient.RemoteClient;
import ru.sbrf.schoolchat.remoteclient.RemoteClientEnumerator;
import ru.sbrf.schoolchat.server.ChatServer;

/**
 * Created by Ivan on 17/11/2016.
 */
public class Disconnect implements Action {
    private static final Disconnect instance = new Disconnect();

    private Disconnect() {

    }

    public static Disconnect getInstance() {
        return instance;
    }

    @Override
    public void perform(Object o, RemoteClient initiator, RemoteClientEnumerator enumerator) {
        if (initiator.isAuthenticated()) {
            TextMessage message = new TextMessage(ChatServer.SYSTEM_NOTIFIER,
                    "user '" + initiator.getUserName() + "' left conversation.");
            enumerator.forEachAuthenticated(c -> c.send(message));
        }
    }
}
