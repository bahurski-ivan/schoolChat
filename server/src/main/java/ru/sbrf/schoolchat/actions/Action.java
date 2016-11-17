package ru.sbrf.schoolchat.actions;

import ru.sbrf.schoolchat.remoteclient.RemoteClient;
import ru.sbrf.schoolchat.remoteclient.RemoteClientEnumerator;

/**
 * Created by Ivan on 17/11/2016.
 */
public interface Action {
    void perform(Object o, RemoteClient initiator, RemoteClientEnumerator enumerator);
}