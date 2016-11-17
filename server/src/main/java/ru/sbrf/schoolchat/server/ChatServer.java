package ru.sbrf.schoolchat.server;

import ru.sbrf.schoolchat.remoteclient.RemoteClient;
import ru.sbrf.schoolchat.remoteclient.RemoteClientEnumerator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by Ivan on 16/11/2016.
 */
public class ChatServer implements Runnable, RemoteClientEnumerator {

    static public final int PORT = 12345;
    static public final String SYSTEM_NOTIFIER = "system";
    private final long keepAliveTimeout;
    private final Thread keepAliveThread = new Thread(new KeepAliveWorker(this));
    private boolean isRunning;
    private ServerSocket serverSocket;
    private MessageEncoder encoder = new MessageEncoder();
    private Map<Socket, ListenerWorker> socketMap = new ConcurrentHashMap<>();
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public ChatServer(long keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    @Override
    public void run() {
        if (!initializeServerSocket())
            return;

        isRunning = true;

        keepAliveThread.start();

        acceptLoop();
    }

    private boolean initializeServerSocket() {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void acceptLoop() {
        while (isRunning) {
            Socket socket;

            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            ListenerWorker worker = new ListenerWorker(this, socket);
            new Thread(worker).start();

            Lock writeLock = rwLock.writeLock();

            writeLock.lock();
            socketMap.put(socket, worker);
            writeLock.unlock();
        }
    }

    boolean isRunning() {
        return isRunning;
    }

    MessageEncoder getEncoder() {
        return encoder;
    }

    void removeFromSocketMap(Socket socket) {
        Lock writeLock = rwLock.writeLock();

        writeLock.lock();
        socketMap.remove(socket);
        writeLock.unlock();
    }

    void forEachListener(Consumer<ListenerWorker> action) {
        Lock readLock = rwLock.readLock();

        try {
            readLock.lock();
            socketMap.forEach((k, v) -> action.accept(v));
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void forEachClient(Consumer<RemoteClient> action) {
        Lock readLock = rwLock.readLock();

        try {
            readLock.lock();
            socketMap.forEach((k, v) -> action.accept(v.getHolder()));
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean anyClientMatch(Predicate<RemoteClient> predicate) {
        Lock readLock = rwLock.readLock();

        try {
            readLock.lock();
            return socketMap.entrySet().stream()
                    .map(kv -> kv.getValue().getHolder())
                    .anyMatch(predicate);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void forEachAuthenticated(Consumer<RemoteClient> action) {
        Lock readLock = rwLock.readLock();

        try {
            readLock.lock();
            socketMap.entrySet().stream()
                    .map(kv -> kv.getValue().getHolder())
                    .filter(RemoteClient::isAuthenticated)
                    .forEach(action);
        } finally {
            readLock.unlock();
        }
    }

    long getKeepAliveTimeout() {
        return keepAliveTimeout;
    }
}
