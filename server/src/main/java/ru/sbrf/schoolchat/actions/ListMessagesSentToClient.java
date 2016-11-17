package ru.sbrf.schoolchat.actions;

import ru.sbrf.schoolchat.objects.ListMessagesResponse;
import ru.sbrf.schoolchat.remoteclient.RemoteClient;
import ru.sbrf.schoolchat.remoteclient.RemoteClientEnumerator;

/**
 * Created by Ivan on 17/11/2016.
 */
public class ListMessagesSentToClient implements Action {
    @Override
    public void perform(Object o, RemoteClient initiator, RemoteClientEnumerator enumerator) {
        if (!initiator.isAuthenticated())
            return;

        initiator.send(new ListMessagesResponse(initiator.getPrivateMessages()));
    }
}
