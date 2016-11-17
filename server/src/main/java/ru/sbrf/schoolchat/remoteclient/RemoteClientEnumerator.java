package ru.sbrf.schoolchat.remoteclient;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by Ivan on 17/11/2016.
 */
public interface RemoteClientEnumerator {
    void forEachClient(Consumer<RemoteClient> action);

    boolean anyClientMatch(Predicate<RemoteClient> predicate);

    void forEachAuthenticated(Consumer<RemoteClient> action);
}