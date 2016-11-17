package ru.sbrf.schoolchat.server;

import ru.sbrf.schoolchat.actions.*;
import ru.sbrf.schoolchat.objects.ListMessagesRequest;
import ru.sbrf.schoolchat.objects.LoginRequest;
import ru.sbrf.schoolchat.objects.SendTextRequest;
import ru.sbrf.schoolchat.remoteclient.RemoteClient;
import ru.sbrf.schoolchat.validators.LoginValidator;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivan on 16/11/2016.
 */
class ListenerWorker implements Runnable {
    private static final Map<Class<?>, Action> handlers = new HashMap<>();

    static {
        handlers.put(LoginRequest.class, new Login(new LoginValidator()));
        handlers.put(SendTextRequest.class, new SendText());
        handlers.put(ListMessagesRequest.class, new ListMessagesSentToClient());
    }

    private final Socket socket;
    private final RemoteClient holder;
    private PrintWriter writer;
    private BufferedReader reader;
    private ChatServer chatServer;
    private long lastActivityMs;

    ListenerWorker(ChatServer chatServer, Socket socket) {
        this.chatServer = chatServer;
        this.socket = socket;
        this.holder = new RemoteClientHolder(this);
    }

    @Override
    public void run() {
        lastActivityMs = currentMs();

        waitAndReadInput();
        close();
    }

    private void waitAndReadInput() {
        if (!initializeStreams())
            return;

        while (chatServer.isRunning()) {
            try {
                String line = reader.readLine();
                lastActivityMs = currentMs();

                if (line == null)
                    break;

                processMessage(line);
            } catch (IOException e) {
                break;
            }
        }

        try {
            reader.close();
        } catch (IOException ignored) {
        }

        writer.close();
    }

    private boolean initializeStreams() {
        InputStream is;
        OutputStream os;

        try {
            is = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                is.close();
            } catch (IOException ignored) {
            }
            return false;
        }

        this.writer = new PrintWriter(os);
        this.reader = new BufferedReader(new InputStreamReader(is));

        return true;
    }

    private void close() {
        chatServer.removeFromSocketMap(socket);

        try {
            socket.close();
        } catch (IOException ignored) {
        }

        handleDisconnect();
    }

    private void handleDisconnect() {
        Disconnect.getInstance().perform(null, holder, chatServer);
    }

    private void processMessage(String message) {
        Object o = chatServer.getEncoder().decode(message);
        if (o != null) {
            Action action = handlers.get(o.getClass());
            if (action != null)
                action.perform(o, holder, chatServer);
        }
    }

    void send(Object message) {
        String text = chatServer.getEncoder().encode(message);
        writer.println(text);
        writer.flush();
    }

    RemoteClient getHolder() {
        return holder;
    }

    long getLastActivityDeltaMs() {
        return currentMs() - lastActivityMs;
    }

    private long currentMs() {
        return System.currentTimeMillis();
    }

    Socket getSocket() {
        return socket;
    }
}
