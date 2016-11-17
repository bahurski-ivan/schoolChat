package ru.sbrf.schoolchat;

import ru.sbrf.schoolchat.objects.ListMessagesResponse;
import ru.sbrf.schoolchat.objects.LoginResponse;
import ru.sbrf.schoolchat.objects.TextMessage;
import ru.sbrf.schoolchat.server.MessageEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by Ivan on 17/11/2016.
 */
class ServerInputReader implements Runnable {
    private static final Map<Class<?>, BiConsumer<ServerInputReader, Object>> handlers = new HashMap<>();

    static {
        handlers.put(ListMessagesResponse.class, ServerInputReader::ListMessagesResponse);
        handlers.put(LoginResponse.class, ServerInputReader::LoginResponse);
        handlers.put(TextMessage.class, ServerInputReader::TextMessage);
    }

    private final ChatClient client;
    private final MessageEncoder encoder;
    private Socket socket;

    ServerInputReader(ChatClient client) {
        this.client = client;
        this.encoder = client.getEncoder();
    }

    @Override
    public void run() {
        BufferedReader br;

        socket = client.getSocket();

        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            String line;
            while (client.isRunning()) {
                line = br.readLine();
                if (line == null)
                    break;
                handleMessage(line);
            }
        } catch (IOException ignored) {
        }

        System.out.println("disconnect from server");
        client.setRunning(false);
    }

    private void handleMessage(String line) {
        Object o = encoder.decode(line);

        if (o != null) {
            BiConsumer<ServerInputReader, Object> handler = handlers.get(o.getClass());
            if (handler != null)
                handler.accept(this, o);
        }
    }

    private void ListMessagesResponse(Object o) {
        ListMessagesResponse response = (ListMessagesResponse) o;

        System.out.println("messages sent to you: ");
        response.getMessageList().forEach(m -> System.out.println("  " + m.toString()));
    }

    private void LoginResponse(Object o) {
        LoginResponse response = (LoginResponse) o;

        if (!response.isOk())
            System.out.println("cannot login: " + response.getMessage());
    }

    private void TextMessage(Object o) {
        System.out.println(o.toString());
    }
}
