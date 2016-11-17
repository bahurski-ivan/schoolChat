package ru.sbrf.schoolchat;

import ru.sbrf.schoolchat.objects.ListMessagesRequest;
import ru.sbrf.schoolchat.objects.LoginRequest;
import ru.sbrf.schoolchat.objects.SendTextRequest;
import ru.sbrf.schoolchat.server.ChatServer;
import ru.sbrf.schoolchat.server.MessageEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ivan on 17/11/2016.
 */
class ChatClient implements Runnable {
    private Socket socket;
    private MessageEncoder encoder = new MessageEncoder();
    private volatile boolean running;
    private PrintWriter writer;
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private Thread listener = new Thread(new ServerInputReader(this));

    @Override
    public void run() {
        if (!initializeIO())
            return;

        String command;
        do {
            command = readCommand();

            if (command == null)
                break;

            Object request = parseCommand(command);
            writer.println(encoder.encode(request));
            writer.flush();

        } while (true);

        close();
    }

    private boolean initializeIO() {
        try {
            socket = new Socket("localhost", ChatServer.PORT);
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("unable to connect to server!");
            return false;
        }

        setRunning(true);
        listener.start();

        return true;
    }

    private String readCommand() {

        String result;

        try {
            result = br.readLine().trim();
        } catch (IOException ignored) {
            return null;
        }

        if (result.equals("/stop"))
            return null;

        return result;
    }

    private Object parseCommand(String command) {
        Object request;

        if (command.startsWith("/w")) {
            int start = command.indexOf('(');
            int end = command.indexOf(')');

            String[] recipients = command.substring(start + 1, end).split(",");
            String message = command.substring(end + 1).trim();

            List<String> recipientList = Arrays.stream(recipients)
                    .map(String::trim)
                    .distinct()
                    .collect(Collectors.toList());

            request = new SendTextRequest(message, recipientList);
        } else if (command.startsWith("/login")) {
            String requestedName = command.substring("/login".length()).trim();
            request = new LoginRequest(requestedName);
        } else if (command.startsWith("/list")) {
            request = new ListMessagesRequest();
        } else
            request = new SendTextRequest(command, Collections.emptyList());

        return request;
    }

    private void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    MessageEncoder getEncoder() {
        return encoder;
    }

    Socket getSocket() {
        return socket;
    }

    boolean isRunning() {
        return running;
    }

    void setRunning(boolean running) {
        this.running = running;
    }
}
