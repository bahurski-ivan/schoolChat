package ru.sbrf.schoolchat.actions;

import ru.sbrf.schoolchat.objects.LoginRequest;
import ru.sbrf.schoolchat.objects.LoginResponse;
import ru.sbrf.schoolchat.objects.TextMessage;
import ru.sbrf.schoolchat.remoteclient.RemoteClient;
import ru.sbrf.schoolchat.remoteclient.RemoteClientEnumerator;
import ru.sbrf.schoolchat.server.ChatServer;
import ru.sbrf.schoolchat.validators.Validator;

/**
 * Created by Ivan on 17/11/2016.
 */
public class Login implements Action {
    private final Validator validator;

    public Login(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void perform(Object o, RemoteClient initiator, RemoteClientEnumerator enumerator) {
        LoginRequest loginRequest = (LoginRequest) o;

        String requestedName = loginRequest.getRequestedName();

        if (!validator.isValid(requestedName)) {
            sendError("invalid name!", initiator);
            return;
        }

        if (enumerator.anyClientMatch(c -> requestedName.equals(c.getUserName()))) {
            sendError(String.format("name: '%s' already used!", requestedName), initiator);
            return;
        }

        initiator.send(new LoginResponse(true, "ok"));
        initiator.setUserName(requestedName);

        TextMessage message = new TextMessage(ChatServer.SYSTEM_NOTIFIER,
                "user '" + requestedName + "' join conversation.");

        enumerator.forEachAuthenticated(c -> c.send(message));
    }

    private void sendError(String message, RemoteClient initiator) {
        initiator.send(new LoginResponse(false, message));
    }
}
