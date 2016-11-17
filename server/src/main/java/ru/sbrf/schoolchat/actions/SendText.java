package ru.sbrf.schoolchat.actions;

import ru.sbrf.schoolchat.objects.SendTextRequest;
import ru.sbrf.schoolchat.objects.TextMessage;
import ru.sbrf.schoolchat.remoteclient.RemoteClient;
import ru.sbrf.schoolchat.remoteclient.RemoteClientEnumerator;

/**
 * Created by Ivan on 17/11/2016.
 */
public class SendText implements Action {
    @Override
    public void perform(Object o, RemoteClient initiator, RemoteClientEnumerator enumerator) {
        if (!initiator.isAuthenticated())
            return;

        SendTextRequest request = (SendTextRequest) o;
        TextMessage message = new TextMessage(initiator.getUserName(), request.getMessage());

        if (request.getRecipients().size() != 0) {
            enumerator.forEachClient(c -> {
                if (c.isAuthenticated() && request.getRecipients().contains(c.getUserName()))
                    c.sendPrivate(message);
            });
        } else
            enumerator.forEachClient(c -> {
                if (c.isAuthenticated())
                    c.send(message);
            });
    }
}
